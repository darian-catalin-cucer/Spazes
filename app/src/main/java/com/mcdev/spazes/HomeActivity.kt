package com.mcdev.spazes

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.drawee.backends.pipeline.Fresco
import com.iammert.library.ui.multisearchviewlib.MultiSearchView
import com.mcdev.spazes.adapter.SpacesAdapter
import com.mcdev.spazes.databinding.ActivityHomeBinding
import com.mcdev.spazes.dto.Spaces
import com.mcdev.spazes.util.BEARER_TOKEN
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), SpacesAdapter.OnItemClickListener {
    val SPACES_URL = "https://twitter.com/i/spaces/"
    private lateinit var binding: ActivityHomeBinding
    var sQuery: String = "spaces"
    val viewModel: SpacesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)//initializing fresco to prevent SimpleDrawee from throwing initialization exception
        binding = ActivityHomeBinding.inflate(LayoutInflater.from(this))
        val view = binding.root
        setContentView(view)

        val adapter = SpacesAdapter(this, this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            this.adapter = adapter
        }

        changeStatusBarColor(R.color.white)

        makeQuery(sQuery)

        binding.swipeRefresh.setOnRefreshListener {
            makeQuery(sQuery)
        }

        binding.searchView.setSearchViewListener(object : MultiSearchView.MultiSearchViewListener{
            override fun onItemSelected(index: Int, s: CharSequence) {
                startLoading()
                makeQuery(s.toString())
                sQuery = s.toString()
            }

            override fun onSearchComplete(index: Int, s: CharSequence) {
                startLoading()
                makeQuery(s.toString())
                sQuery = s.toString()
            }

            override fun onSearchItemRemoved(index: Int) {
                //TODO("Not yet implemented")
            }

            override fun onTextChanged(index: Int, s: CharSequence) {
                sQuery = s.toString()
            }

        })

        //collect
        lifecycleScope.launchWhenStarted {
            viewModel.search.collect{
                when (it) {
                    is SpacesEventListener.Success -> {
                        stopLoading()
                        adapter.submitSpacesList(it.data.data!!)
                        adapter.submitUsersList(it.data.includes?.users!!)
                    }
                    is SpacesEventListener.Failure -> {
                        stopLoading()
                    }
                    is SpacesEventListener.Loading -> {
                        startLoading()
                    }
                    is SpacesEventListener.Empty -> {
                        showEmpty()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun startLoading() {
        binding.swipeRefresh.isRefreshing = true
        binding.emptyLottie.visibility = View.GONE
        binding.recyclerMessage.visibility = View.GONE
        //binding.recyclerView.visibility = View.GONE
        //binding.recyclerMessage.visibility = View.GONE
    }

    private fun stopLoading() {
        binding.swipeRefresh.isRefreshing = false
        binding.recyclerView.visibility = View.VISIBLE
        binding.emptyLottie.visibility = View.GONE
        binding.recyclerMessage.visibility = View.GONE
    }


    private fun showEmpty() {
        binding.swipeRefresh.isRefreshing = false
        binding.recyclerView.visibility = View.GONE
        binding.recyclerMessage.visibility = View.VISIBLE
        binding.emptyLottie.visibility = View.VISIBLE
        binding.recyclerMessage.text = applicationContext.getString(R.string.no_spaces_found)
    }

    private fun makeQuery(query: String) {
        viewModel.searchSpaces(

            "BEARER $BEARER_TOKEN",
            query,
            "created_at,creator_id,ended_at,host_ids,id,invited_user_ids,is_ticketed,lang,participant_count,scheduled_start,speaker_ids,started_at,state,title,topic_ids,updated_at",
            "created_at,description,entities,id,location,name,pinned_tweet_id,profile_image_url,protected,public_metrics,url,username,verified,withheld",
            "invited_user_ids,speaker_ids,creator_id,host_ids"
        )
    }

    override fun onItemClick(position: Int) {
        //TODO("Not yet implemented")
    }

    override fun onGoToClick(spaces: Spaces, position: Int) {
        val link = SPACES_URL+spaces.id
        Log.d("TAG", "onBindViewHolder: link is : $link")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.recyclerView.adapter = null
    }
}