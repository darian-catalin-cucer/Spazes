package com.mcdev.spazes.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcdev.spazes.*
import com.mcdev.spazes.adapter.SpacesAdapter
import com.mcdev.spazes.databinding.ActivityUserSpacesBinding
import com.mcdev.spazes.enums.LoadAction
import com.mcdev.spazes.events.SpacesListEventListener
import com.mcdev.spazes.repository.FirebaseEventListener
import com.mcdev.spazes.util.BEARER_TOKEN
import com.mcdev.spazes.viewmodel.SpacesViewModel
import com.mcdev.twitterapikit.`object`.Space
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class UserSpacesActivity : AppCompatActivity(), SpacesAdapter.OnSpacesItemClickListener {
    private lateinit var binding: ActivityUserSpacesBinding
    private val viewModel: SpacesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSpacesBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        changeStatusBarColor(R.color.white)

        val loadAction = intent.extras?.get("loadAction") as LoadAction
        val userTwitterId = intent.extras?.get("user_twitter_id").toString()
        val userFirebaseId = intent.extras?.get("user_firebase_id").toString()

        val adapter = SpacesAdapter(this, this)
        binding.userSpacesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@UserSpacesActivity)
            itemAnimator = null
            this.adapter = adapter
        }

        when (loadAction) {
            LoadAction.MY_SPACES ->{
                binding.titleText.text = getString(R.string.my_spaces)
                loadMySpaces(userTwitterId)
            }
            LoadAction.FAVE_HOSTS_SPACES -> {
                binding.titleText.text = getString(R.string.favorite_hosts_spaces)
                loadFaveHostSpaces(userTwitterId, userFirebaseId)
            }
            else -> {}
        }
        binding.swipeRefresh.setOnRefreshListener {
            when (loadAction) {
                LoadAction.MY_SPACES -> loadMySpaces(userTwitterId)
                LoadAction.FAVE_HOSTS_SPACES -> loadFaveHostSpaces(userTwitterId, userFirebaseId)
                else -> {}
            }
        }

        binding.userSpacesBackBtn.setOnClickListener {
            finish()
        }


        //collect my spaces
        lifecycleScope.launchWhenStarted {
            viewModel.search.collect {
                when (it) {
                    is SpacesListEventListener.Success -> {
                        stopLoading()
                        it.data?.let { it1 -> adapter.submitResponse(it1) }
                    }
                    is SpacesListEventListener.Failure -> {
                        stopLoading()
                        Toast.makeText(this@UserSpacesActivity, "Failed.", Toast.LENGTH_SHORT).show()
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

        lifecycleScope.launchWhenStarted {
            viewModel.fireStoreListener.collect{
                when (it) {
                    is FirebaseEventListener.DocumentSuccess -> {
//                        stopLoading(binding.swipeRefresh, binding.recyclerMessage, binding.userSpacesRecyclerView)

                        val hostIds: ArrayList<HashMap<String, String>> = it.data?.get("fave_hosts") as ArrayList<HashMap<String, String>>
                        val ids = getTheIDs(hostIds)
                        //if the id list is empty or null, just display the empty message, otherwise you will be making query to the API with no ID at all which will throw an error

                        if (ids.isBlank()) {
                            showEmpty(binding.swipeRefresh, binding.recyclerMessage, binding.userSpacesRecyclerView, R.string.no_spaces_found)
                        } else {
                            getFaveHostSpaces(ids)
                        }
                    }
                    is FirebaseEventListener.Failure -> {
                        stopLoading(binding.swipeRefresh, binding.recyclerMessage, binding.userSpacesRecyclerView)
                        Toast.makeText(this@UserSpacesActivity, "Failed.", Toast.LENGTH_SHORT).show()
                    }
                    is FirebaseEventListener.Empty -> {
                        stopLoading(binding.swipeRefresh, binding.recyclerMessage, binding.userSpacesRecyclerView)
                        showEmpty(binding.swipeRefresh, binding.recyclerMessage, binding.userSpacesRecyclerView, R.string.no_spaces_found)
                    }
                    is FirebaseEventListener.Loading -> {
                        startLoading(binding.swipeRefresh, binding.recyclerMessage)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadMySpaces(id: String) {
        viewModel.searchSpacesByCreatorIds(ids = id)
    }

    private fun loadFaveHostSpaces(userTwitterId: String, userFirebaseId: String) {
        getFaveHosts(userFirebaseId)
    }

    private fun getFaveHosts(userId: String) {
        viewModel.getFaveHosts(userId)
    }

    private fun getFaveHostSpaces(ids: String) {
        viewModel.searchSpacesByCreatorIds(ids = ids)
    }

    private fun getTheIDs(result: ArrayList<HashMap<String, String>>): String{
        val theIDs = mutableListOf<String>()
        for (document in result) {
            theIDs.add(document["hostId"]!!)
        }
        //joinToString method will put them in a string and separator will separate without whitespaces
        return  theIDs.joinToString(separator = ",")
    }

    private fun startLoading() {
        binding.swipeRefresh.isRefreshing = true
//        binding.emptyLottie.visibility = View.GONE
//        binding.emptyFeatureLottie.visibility = View.GONE
        binding.recyclerMessage.visibility = View.GONE
        //binding.recyclerView.visibility = View.GONE
        //binding.recyclerMessage.visibility = View.GONE
    }

    private fun stopLoading() {
        binding.swipeRefresh.isRefreshing = false
        binding.userSpacesRecyclerView.visibility = View.VISIBLE
//        binding.emptyLottie.visibility = View.GONE
//        binding.emptyFeatureLottie.visibility = View.GONE
        binding.recyclerMessage.visibility = View.GONE
    }

    private fun showEmpty(message: Int) {
//        when (message) {
//            R.string.no_spaces_found -> {
//                binding.emptyFeatureLottie.visibility = View.GONE
//                binding.emptyLottie.visibility = View.VISIBLE
//            }
//            else -> {
//                binding.emptyLottie.visibility = View.GONE
//                binding.emptyFeatureLottie.visibility = View.GONE
//            }
//        }
        binding.recyclerMessage.text = applicationContext.getString(message)
        binding.swipeRefresh.isRefreshing = false
        binding.userSpacesRecyclerView.visibility = View.GONE
        binding.recyclerMessage.visibility = View.VISIBLE
    }

    override fun onSpacesItemClick(spaces: Space, position: Int) {
        val link = SPACES_URL + spaces.id
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }

    override fun onGoToClick(spaces: Space, position: Int) {
        val link = SPACES_URL + spaces.id
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}