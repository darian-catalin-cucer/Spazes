package com.mcdev.spazes.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mcdev.spazes.R
import com.mcdev.spazes.adapter.UserAdapter
import com.mcdev.spazes.changeStatusBarColor
import com.mcdev.spazes.databinding.ActivityUsersBinding
import com.mcdev.spazes.databinding.BottomsheetLayoutBinding
import com.mcdev.spazes.enums.LoadAction
import com.mcdev.spazes.events.UserListEventListener
import com.mcdev.spazes.events.UserSingleEventListener
import com.mcdev.spazes.getOriginalTwitterAvi
import com.mcdev.spazes.model.FaveHost
import com.mcdev.spazes.util.DBCollections
import com.mcdev.spazes.viewmodel.SpacesViewModel
import com.mcdev.twitterapikit.`object`.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@AndroidEntryPoint
class UsersActivity : AppCompatActivity(), UserAdapter.OnUserItemClickListener {
    private lateinit var binding: ActivityUsersBinding
    private val viewModel: SpacesViewModel by viewModels()
    private var userAdapter: UserAdapter? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        changeStatusBarColor(R.color.white)

        val loadAction = intent.extras?.get("loadAction") as LoadAction
        val userTwitterId = intent.extras?.get("user_twitter_id").toString()
        userId = intent.extras?.get("user_id").toString()

        userAdapter = UserAdapter(this, this)
        binding.usersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@UsersActivity)
//            itemAnimator = null//todo uncomment when it is causing crash
            this.adapter = userAdapter
        }

        binding.userBackBtn.setOnClickListener {
            finish()
        }

        binding.addFaveHostFab.setOnClickListener {
            showBottomSheet()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.findUserByUsername.collect {
                when (it) {
                    is UserSingleEventListener.Success -> {
                        Log.d(
                            "TAG",
                            "onCreate: UserSingle Event listener is success : ${it.data.toString()}"
                        )
                    }
                    is UserSingleEventListener.Failure -> {
                        Log.d("TAG", "onCreate: UserSingle Event listener is failed")
                    }
                    is UserSingleEventListener.Empty -> {
                        Log.d("TAG", "onCreate: UserSingle Event listener is empty")
                    }
                    is UserSingleEventListener.Loading -> {
                        Log.d("TAG", "onCreate: UserSingle Event listener is loading")
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.findUsersByUserNames.collect {
                when (it) {
                    is UserListEventListener.Success -> {
                        it.data?.let { it1 -> userAdapter!!.submitResponse(it1) }
                        Log.d(
                            "TAG",
                            "onCreate: UserSingle Event listener is success : ${it.data.toString()}"
                        )
                    }
                    is UserListEventListener.Failure -> {
                        Log.d("TAG", "onCreate: UserList Event listener is failed")
                    }
                    is UserListEventListener.Empty -> {
                        Log.d("TAG", "onCreate: UserList Event listener is empty")
                    }
                    is UserListEventListener.Loading -> {
                        Log.d("TAG", "onCreate: UserList Event listener is loading")
                    }
                }
            }
        }
    }

    private fun showBottomSheet() {
        val addHostBottomSheet = BottomSheetDialog(this)
        val bottomSheetBinding = BottomsheetLayoutBinding.inflate(layoutInflater)
        addHostBottomSheet.setContentView(bottomSheetBinding.root)
        addHostBottomSheet.show()


        bottomSheetBinding.bottomSheetSearch.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrBlank().not()) {
                    getUserByUsername(query!!)

                    lifecycleScope.launchWhenStarted {
                        viewModel.findUserByUsername.collect {
                            when (it) {
                                is UserSingleEventListener.Success -> {
                                    val user: User = it.data?.data!!
                                    Log.d(
                                        "TAG",
                                        "onCreate: UserSingle Event listener is success : $user"
                                    )
                                    bottomSheetBinding.userDetails.addRemoveBtn.setActualImageResource(
                                        R.drawable.plus
                                    )
                                    bottomSheetBinding.userDetails.userAvi.setImageURI(user.profileImageUrl?.getOriginalTwitterAvi())
                                    bottomSheetBinding.userDetails.userName.text = user.username
                                    bottomSheetBinding.userDetails.userDisplayName.text = user.name
                                    if (user.verified) {
                                        bottomSheetBinding.userDetails.userVerifiedBadge.visibility =
                                            View.VISIBLE
                                    } else {
                                        bottomSheetBinding.userDetails.userVerifiedBadge.visibility =
                                            View.GONE
                                    }

                                    bottomSheetBinding.progressAnimationView.visibility = View.GONE
                                    bottomSheetBinding.userDetails.root.visibility = View.VISIBLE

                                    //onClick listener
                                    bottomSheetBinding.userDetails.addRemoveBtn.setOnClickListener {
                                        val faveHost = FaveHost(user.id)
                                        val hashMap = hashMapOf("fave_hosts" to arrayListOf(faveHost))
                                        addFaveHost(userId!!, faveHost)
                                    }
                                }

                                is UserSingleEventListener.Failure -> {
                                    Log.d("TAG", "onCreate: UserSingle Event listener is failed")
                                    bottomSheetBinding.progressAnimationView.visibility = View.GONE
                                    bottomSheetBinding.userDetails.root.visibility = View.GONE
                                }
                                is UserSingleEventListener.Empty -> {
                                    Log.d("TAG", "onCreate: UserSingle Event listener is empty")
                                    bottomSheetBinding.progressAnimationView.visibility = View.GONE
                                    bottomSheetBinding.userDetails.root.visibility = View.GONE
                                }
                                is UserSingleEventListener.Loading -> {
                                    bottomSheetBinding.progressAnimationView.visibility =
                                        View.VISIBLE
                                    bottomSheetBinding.userDetails.root.visibility = View.GONE
                                    Log.d("TAG", "onCreate: UserSingle Event listener is loading")
                                }
                            }
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //TODO("Not yet implemented")
                return false
            }


        })

//        val addHostBottomSheet = UserBottomSheetFragment()
//        addHostBottomSheet.show(supportFragmentManager, addHostBottomSheet.tag)
    }

    private fun getUserByUsername(username: String) {
        viewModel.getUsersByUsername(username = username)
    }

    private fun getUsersByUsernames(usernames: String) {
        viewModel.getUsersByUsernames(usernames = usernames)
    }

    private fun addFaveHost(userId: String, data: Any) {
        viewModel.addUsers(userId, data)
    }
    override fun onUserItemClick(user: User, position: Int) {
//        TODO("Not yet implemented")
    }
}