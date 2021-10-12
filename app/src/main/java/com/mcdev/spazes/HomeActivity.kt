package com.mcdev.spazes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.facebook.drawee.backends.pipeline.Fresco
import com.iammert.library.ui.multisearchviewlib.MultiSearchView
import com.mcdev.SpacesState
import com.mcdev.data.SpacesResponse
import com.mcdev.spazes.adapter.SpacesAdapter
import com.mcdev.spazes.databinding.ActivityHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var adapter: SpacesAdapter? = null
    var sQuery: String = "space"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)//initializing fresco to prevent SimpleDrawee from throwing initialization exception
        binding = ActivityHomeBinding.inflate(LayoutInflater.from(this))
        val view = binding.root
        setContentView(view)

        changeStatusBarColor(R.color.white)
        binding.recyclerView.layoutManager = LinearLayoutManager(this@HomeActivity)

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


    }

    fun makeQuery(searchQuery: String, state: SpacesState = SpacesState.ALL) {
        CoroutineScope(Dispatchers.Main).launch {
            val spacesResponse: Response? = searchSpaces(searchQuery, state)

            if (spacesResponse != null) {
                binding.swipeRefresh.isRefreshing = false
                if (spacesResponse.code == 200) {
                    Log.d("TAG", "makeQuery: query was successful")
                    val spaces = SpacesResponse.parse(spacesResponse.body!!)

                    adapter = SpacesAdapter(this@HomeActivity, spaces)
                    updateUI(adapter!!)
                } else {
                    Log.d("TAG", "makeQuery: query was not successful with code ${spacesResponse.code}")
                }
            }
        }
    }

    private fun updateUI(adapter: SpacesAdapter) {
        stopLoading()

        if (adapter.itemCount < 1) {
            showNotFound()
        } else {
            YoYo.with(Techniques.SlideInUp)
                .duration(700)
                .repeat(0)
                .playOn(binding.recyclerView)

            binding.recyclerView.adapter = adapter
        }

    }

    private fun startLoading() {
        binding.swipeRefresh.isRefreshing = true
        binding.recyclerView.visibility = View.GONE
        binding.recyclerMessage.visibility = View.GONE
    }

    private fun stopLoading() {
        binding.swipeRefresh.isRefreshing = false
        binding.recyclerView.visibility = View.VISIBLE
    }


    private fun showNotFound() {
        binding.recyclerView.visibility = View.GONE
        binding.recyclerMessage.visibility = View.VISIBLE
        binding.recyclerMessage.text = applicationContext.getString(R.string.no_results_found)



//        //Start animation
//        binding.recyclerLottieMessage.apply {
//            setAnimation(R.raw.not_found)
//            visibility = View.VISIBLE
//            playAnimation()
//            setMaxFrame(59)
//            //loop animation when it ends
//            addAnimatorListener(object : Animator.AnimatorListener{
//                override fun onAnimationStart(animation: Animator?) {
//                    //TODO("Not yet implemented")
//                }
//
//                override fun onAnimationEnd(animation: Animator?) {
//                    binding.recyclerLottieMessage.apply {
//                        setMinAndMaxFrame(38,59)
//                        repeatCount = LottieDrawable.INFINITE
//                        repeatMode = LottieDrawable.REVERSE
//                        resumeAnimation()
//
//                    }
//                }
//
//                override fun onAnimationCancel(animation: Animator?) {
//                    //TODO("Not yet implemented")
//                }
//
//                override fun onAnimationRepeat(animation: Animator?) {
//                    //TODO("Not yet implemented")
//                }
//
//            })
//        }
    }

//    private fun showNoInternetConnectivity() {
//        binding.recyclerLottieMessage.apply {
//            visibility = View.VISIBLE
//            setAnimation(R.raw.no_internet)
//            setMinAndMaxFrame(1, 150)
//            playAnimation()
//            repeatCount = INFINITE
//            repeatMode = RESTART
//        }
//    }
}