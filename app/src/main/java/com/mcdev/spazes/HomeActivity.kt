package com.mcdev.spazes

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.iammert.library.ui.multisearchviewlib.MultiSearchView
import com.mcdev.spazes.adapter.SpacesAdapter
import com.mcdev.spazes.databinding.ActivityHomeBinding
import com.mcdev.spazes.di.AppModule
import com.mcdev.spazes.repository.FirebaseEventListener
import com.mcdev.spazes.util.BEARER_TOKEN
import com.mcdev.spazes.util.DBCollections
import com.mcdev.twitterapikit.`object`.Space
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlin.math.log

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), SpacesAdapter.OnItemClickListener {
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!
    var sQuery: String = "space"
    private val viewModel: SpacesViewModel by viewModels()

    private var refreshType: RefreshType = RefreshType.featured_refresh

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)//initializing fresco to prevent SimpleDrawee from throwing initialization exception
        _binding = ActivityHomeBinding.inflate(LayoutInflater.from(this))
        val view = binding.root
        setContentView(view)

        binding.lineChartLottie.frame = 130

        val adapter = SpacesAdapter(this, this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            itemAnimator = null
            this.adapter = adapter
        }

        changeStatusBarColor(R.color.white)
        /*get featured spaces*/
        viewModel.getFeaturedSpaces()

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
                        it.data?.let { it1 -> adapter.submitResponse(it1) }
                    }
                    is SpacesListEventListener.Failure -> {
                        stopLoading()
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
                            showEmpty(R.string.no_featured_spaces)
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
                }
            }
        }

        binding.featuredLay.setOnClickListener {
            binding.fireLottie.playAnimation()
            viewModel.getFeaturedSpaces()
            refreshType = RefreshType.featured_refresh
        }

        binding.trendingLay.setOnClickListener {
            binding.lineChartLottie.playAnimation()
            viewModel.getTrendingSpaces()
            refreshType = RefreshType.trending_refresh
        }

        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.settingsBtn.setOnClickListener {
            Toast.makeText(this, "Coming soon...", Toast.LENGTH_SHORT).show()
        }

    }

    private fun startLoading() {
        binding.swipeRefresh.isRefreshing = true
        binding.emptyLottie.visibility = View.GONE
        binding.emptyFeatureLottie.visibility = View.GONE
        binding.recyclerMessage.visibility = View.GONE
        //binding.recyclerView.visibility = View.GONE
        //binding.recyclerMessage.visibility = View.GONE
    }

    private fun stopLoading() {
        binding.swipeRefresh.isRefreshing = false
        binding.recyclerView.visibility = View.VISIBLE
        binding.emptyLottie.visibility = View.GONE
        binding.emptyFeatureLottie.visibility = View.GONE
        binding.recyclerMessage.visibility = View.GONE
    }


    private fun showEmpty(message: Int) {
        when (message) {
            R.string.no_spaces_found -> {
                binding.emptyFeatureLottie.visibility = View.GONE
                binding.emptyLottie.visibility = View.VISIBLE
            }
            else -> {
                binding.emptyLottie.visibility = View.GONE
                binding.emptyFeatureLottie.visibility = View.GONE
            }
        }
        binding.recyclerMessage.text = applicationContext.getString(message)
        binding.swipeRefresh.isRefreshing = false
        binding.recyclerView.visibility = View.GONE
        binding.recyclerMessage.visibility = View.VISIBLE
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

    override fun onItemClick(spaces: Space, position: Int) {
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
}