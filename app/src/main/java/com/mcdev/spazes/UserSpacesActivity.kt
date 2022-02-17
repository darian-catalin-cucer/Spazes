package com.mcdev.spazes

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcdev.spazes.adapter.SpacesAdapter
import com.mcdev.spazes.databinding.ActivityUserSpacesBinding
import com.mcdev.spazes.util.BEARER_TOKEN
import com.mcdev.twitterapikit.`object`.Space
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class UserSpacesActivity : AppCompatActivity(), SpacesAdapter.OnItemClickListener {
    private lateinit var binding: ActivityUserSpacesBinding
    private val viewModel: SpacesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSpacesBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        val loadAction = intent.extras?.get("loadAction") as LoadAction
        val userid = intent.extras?.get("user_twitter_id").toString()

        val adapter = SpacesAdapter(this, this)
        binding.userSpacesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@UserSpacesActivity)
            itemAnimator = null
            this.adapter = adapter
        }

        when (loadAction) {
            LoadAction.MY_SPACES ->{
                binding.titleText.text = "My Spaces"
                loadMySpaces(userid)
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
    }

    private fun loadMySpaces(id: String) {
        viewModel.searchSpacesByCreatorIds(
            "BEARER $BEARER_TOKEN",
             id,
            "created_at,creator_id,ended_at,host_ids,id,invited_user_ids,is_ticketed,lang,participant_count,scheduled_start,speaker_ids,started_at,state,title,topic_ids,updated_at",
            "created_at,description,entities,id,location,name,pinned_tweet_id,profile_image_url,protected,public_metrics,url,username,verified,withheld",
            "invited_user_ids,speaker_ids,creator_id,host_ids",
            "description,id,name"
        )
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
        binding.userSpacesRecyclerView.visibility = View.VISIBLE
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
        binding.userSpacesRecyclerView.visibility = View.GONE
        binding.recyclerMessage.visibility = View.VISIBLE
    }

    override fun onItemClick(spaces: Space, position: Int) {
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