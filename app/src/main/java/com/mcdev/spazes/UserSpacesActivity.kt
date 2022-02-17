package com.mcdev.spazes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcdev.spazes.adapter.SpacesAdapter
import com.mcdev.spazes.databinding.ActivityUserSpacesBinding
import com.mcdev.twitterapikit.`object`.Space

class UserSpacesActivity : AppCompatActivity(), SpacesAdapter.OnItemClickListener {
    private lateinit var binding: ActivityUserSpacesBinding
    private val viewModel: SpacesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSpacesBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        val adapter = SpacesAdapter(this, this)
        binding.userSpacesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@UserSpacesActivity)
            itemAnimator = null
            this.adapter = adapter
        }
    }

    override fun onItemClick(spaces: Space, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onGoToClick(spaces: Space, position: Int) {
        TODO("Not yet implemented")
    }
}