package com.mcdev.spazes.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeActivity
import com.dolatkia.animatedThemeManager.ThemeManager
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.remote.Datastore
import com.iammert.library.ui.multisearchviewlib.MultiSearchView
import com.mcdev.spazes.*
import com.mcdev.spazes.adapter.SpacesAdapter
import com.mcdev.spazes.databinding.ActivityHomeBinding
import com.mcdev.spazes.enums.RefreshType
import com.mcdev.spazes.events.SpacesListEventListener
import com.mcdev.spazes.repository.FirebaseEventListener
import com.mcdev.spazes.theme.BaseTheme
import com.mcdev.spazes.theme.DarkTheme
import com.mcdev.spazes.theme.DefaultTheme
import com.mcdev.spazes.theme.LightTheme
import com.mcdev.spazes.util.BEARER_TOKEN
import com.mcdev.spazes.util.DBCollections
import com.mcdev.spazes.viewmodel.DatastoreViewModel
import com.mcdev.spazes.viewmodel.SpacesViewModel
import com.mcdev.twitterapikit.`object`.Space
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import spencerstudios.com.bungeelib.Bungee

@AndroidEntryPoint
class HomeActivity : ThemeActivity(), SpacesAdapter.OnSpacesItemClickListener {
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!
    var sQuery: String = "space"
    private val viewModel: SpacesViewModel by viewModels()
    private val dataStoreViewModel: DatastoreViewModel by viewModels()
    private var showAppIntro: Boolean? = false
    private var themeMode : AppTheme = DefaultTheme()
    private lateinit var spacesAdapter : SpacesAdapter

    private var refreshType: RefreshType = RefreshType.featured_refresh
    override fun getStartTheme(): AppTheme {
        var getTheme : String? = null
        runBlocking {
            getTheme = dataStoreViewModel.readDatastore("themeMode")
        }


        themeMode =  when (getTheme) {
            "0" -> DefaultTheme()
            "1" -> LightTheme()
            "2" -> DarkTheme()
            else -> {
                DefaultTheme()
            }
        }

        return themeMode
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)//initializing fresco to prevent SimpleDrawee from throwing initialization exception
        _binding = ActivityHomeBinding.inflate(LayoutInflater.from(this))
        val view = binding.root
        setContentView(view)

        var getTheme: String? = null
        runBlocking {
            showAppIntro = dataStoreViewModel.readAppIntroDatastore("show_app_intro")
            getTheme = dataStoreViewModel.readDatastore("themeMode")
        }


        themeMode =  when (getTheme) {
            "0" -> DefaultTheme()
            "1" -> LightTheme()
            "2" -> DarkTheme()
            else -> {
                DefaultTheme()
            }
        }

        if (showAppIntro == null || showAppIntro == true) {
            startActivity(Intent(this, AppIntro::class.java))
            Bungee.zoomIn(this)
        }

        spacesAdapter = SpacesAdapter(this, this, themeMode)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            itemAnimator = null
            this.adapter = spacesAdapter
        }


        ThemeManager.instance.getCurrentLiveTheme().observe(this) {
            spacesAdapter = SpacesAdapter(this, this, it)
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(this@HomeActivity)
                itemAnimator = null
                this.adapter = spacesAdapter
            }

            when (this.refreshType) {
                RefreshType.featured_refresh -> viewModel.getFeaturedSpaces()
                RefreshType.trending_refresh -> viewModel.getTrendingSpaces()
                RefreshType.search_refresh -> makeQuery(sQuery)
            }

            val tt = it as BaseTheme
            changeStatusBarColor(tt.statusBarColor())

        }

        /*get featured spaces*/
        viewModel.getFeaturedSpaces()

        binding.swipeRefresh.setProgressViewOffset(true, 200, 300)
        binding.swipeRefresh.setOnRefreshListener {
            when (this.refreshType) {
                RefreshType.featured_refresh -> viewModel.getFeaturedSpaces()
                RefreshType.trending_refresh -> viewModel.getTrendingSpaces()
                RefreshType.search_refresh -> makeQuery(sQuery)
            }
        }

        binding.searchView.setSearchViewListener(object : MultiSearchView.MultiSearchViewListener {
            override fun onItemSelected(index: Int, s: CharSequence) {
                startLoading()
                this@HomeActivity.refreshType = RefreshType.search_refresh
                makeQuery(s.toString())
                sQuery = s.toString()
            }

            override fun onSearchComplete(index: Int, s: CharSequence) {
                startLoading()
                this@HomeActivity.refreshType = RefreshType.search_refresh
                makeQuery(s.toString())
                sQuery = s.toString()
            }

            override fun onSearchItemRemoved(index: Int) {
                //TODO("Not yet implemented")
            }

            override fun onTextChanged(index: Int, s: CharSequence) {
                this@HomeActivity.refreshType = RefreshType.search_refresh
                sQuery = s.toString()
            }

        })

        //collect search
        lifecycleScope.launchWhenStarted {
            viewModel.search.collect {
                when (it) {
                    is SpacesListEventListener.Success -> {
                        stopLoading()
                        it.data?.let { it1 -> spacesAdapter.submitResponse(it1) }
                    }
                    is SpacesListEventListener.Failure -> {
                        stopLoading()
                        Toast.makeText(this@HomeActivity, "Failed, Try again!", Toast.LENGTH_SHORT).show()
                    }
                    is SpacesListEventListener.Loading -> {
                        startLoading()
                    }
                    is SpacesListEventListener.Empty -> {
                        showEmpty(it.message!!)
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.fireStoreListener.collect{
                when (it) {
                    is FirebaseEventListener.Success -> {
                        val theIDS = getTheIDs(it.data!!)
                        //if the id list is empty or null, just display the empty message, otherwise you will be making query to the API with no ID at all which will throw an error
                        if (theIDS.isEmpty()) {
                            when (refreshType) {
                                RefreshType.featured_refresh -> showEmpty(R.string.no_featured_spaces)
                                RefreshType.trending_refresh -> showEmpty(R.string.no_trending_space)
                                RefreshType.search_refresh -> showEmpty(R.string.no_spaces_found)
                            }
                        } else {
                            querySpacesByListOfIds(theIDS, DBCollections.Featured.toString())
                        }
                    }
                    is FirebaseEventListener.Failure -> {
                        stopLoading()
                        Toast.makeText(this@HomeActivity, "Failed.", Toast.LENGTH_SHORT).show()
                    }
                    is FirebaseEventListener.Empty -> {
                        stopLoading()
                        showEmpty(it.message!!)
                    }
                    is FirebaseEventListener.Loading -> {
                        startLoading()
                    }
                    else -> {
                        Log.d("TAG", "onCreate: else branch called")
                    }
                }
            }
        }

//        binding.featuredLay.setOnClickListener {
//            binding.fireLottie.playAnimation()
//            viewModel.getFeaturedSpaces()
//            refreshType = RefreshType.featured_refresh
//        }

//        binding.trendingLay.setOnClickListener {
//            binding.lineChartLottie.playAnimation()
//            viewModel.getTrendingSpaces()
//            refreshType = RefreshType.trending_refresh
//        }

//        binding.profileBtn.apply {
//            icon = R.drawable.profile
//            bgColor = R.color.tritone_purple_bg
//        }

        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            Bungee.zoomIn(this)
        }

        binding.settingsBtn.setOnClickListener {
            startActivity(Intent(this@HomeActivity, SettingsActivity::class.java))
            finish() //todo implement this in a better way
            Bungee.zoomIn(this)
        }

    }

    private fun startLoading() {
        binding.swipeRefresh.isRefreshing = true
        binding.noSpaceComponentView.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
    }

    private fun stopLoading() {
        binding.swipeRefresh.isRefreshing = false
        binding.recyclerView.visibility = View.VISIBLE
        binding.noSpaceComponentView.visibility = View.GONE
    }


    private fun showEmpty(message: Int) {
        binding.noSpaceComponentView.apply {
            this.lottieMessage = resources.getString(message)
            visibility = View.VISIBLE
        }
        binding.swipeRefresh.isRefreshing = false
        binding.recyclerView.visibility = View.GONE
    }

    private fun makeQuery(query: String) {
        viewModel.searchSpaces(

            "BEARER $BEARER_TOKEN",
            query,
            "created_at,creator_id,ended_at,host_ids,id,invited_user_ids,is_ticketed,lang,participant_count,scheduled_start,speaker_ids,started_at,state,title,topic_ids,updated_at",
            "created_at,description,entities,id,location,name,pinned_tweet_id,profile_image_url,protected,public_metrics,url,username,verified,withheld",
            "invited_user_ids,speaker_ids,creator_id,host_ids",
            "description,id,name"
        )
    }

    private fun querySpacesByListOfIds(ids: String, firestoreCollection: String) {
        viewModel.searchSpacesByIds(
            "BEARER $BEARER_TOKEN",
            ids,
            "created_at,creator_id,ended_at,host_ids,id,invited_user_ids,is_ticketed,lang,participant_count,scheduled_start,speaker_ids,started_at,state,title,topic_ids,updated_at",
            "created_at,description,entities,id,location,name,pinned_tweet_id,profile_image_url,protected,public_metrics,url,username,verified,withheld",
            "invited_user_ids,speaker_ids,creator_id,host_ids",
            "description,id,name",
            firestoreCollection
        )
    }

    private fun getTheIDs(result: QuerySnapshot): String{
        val spacesIds = mutableListOf<String>()
        for (document in result) {
            spacesIds.add(document.data["space_id"].toString())
        }
        //joinToString method will put them in a string and separator will separate without whitespaces
        return  spacesIds.joinToString(separator = ",")
    }

    override fun onSpacesItemClick(spaces: Space, position: Int) {
        val link = SPACES_URL + spaces.id
        Log.d("TAG", "onBindViewHolder: link is : $link")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }

    override fun onGoToClick(spaces: Space, position: Int) {
        val link = SPACES_URL + spaces.id
        Log.d("TAG", "onBindViewHolder: link is : $link")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }


    override fun onDestroy() {
        binding.recyclerView.adapter = null
        _binding = null
        super.onDestroy()
    }

    override fun syncTheme(appTheme: AppTheme) {
        themeMode = appTheme
        val theme = appTheme as BaseTheme
        binding.root.setBackgroundColor(theme.activityBgColor(this))
        binding.tv.setTextColor(resources.getColor(appTheme.textColor(), this.theme))

        binding.searchView.apply {
            setSearchTextColor(theme.searchTextColor(this@HomeActivity))
            setSelectedTabColor(theme.searchSelectedTabColor(this@HomeActivity))
            setHintTextColor(theme.searchHintColor(this@HomeActivity))
            setClearIconColor(theme.searchClearIconColor(this@HomeActivity))
            setSearchIconColor(theme.searchIconColor(this@HomeActivity))
        }

        if (appTheme.id() == SpazesThemeMode.DEFAULT_MODE.value) {
            binding.searchView.background = ResourcesCompat.getDrawable(resources, R.drawable.light_mode_search_bg, this@HomeActivity.theme)
        } else if (appTheme.id() == SpazesThemeMode.LIGHT_MODE.value) {
            binding.searchView.background = ResourcesCompat.getDrawable(resources, R.drawable.light_mode_search_bg, this@HomeActivity.theme)
        } else if (appTheme.id() == SpazesThemeMode.DARK_MODE.value) {
            binding.searchView.background = ResourcesCompat.getDrawable(resources, R.drawable.dark_mode_search_bg, this@HomeActivity.theme)
        }

        binding.noSpaceComponentView.theme = appTheme
    }

}