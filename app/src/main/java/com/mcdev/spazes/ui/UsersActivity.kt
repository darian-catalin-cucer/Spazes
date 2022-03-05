package com.mcdev.spazes.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mcdev.spazes.*
import com.mcdev.spazes.adapter.UserAdapter
import com.mcdev.spazes.databinding.ActivityUsersBinding
import com.mcdev.spazes.databinding.BottomsheetLayoutBinding
import com.mcdev.spazes.enums.LoadAction
import com.mcdev.spazes.events.UserListEventListener
import com.mcdev.spazes.events.UserSingleEventListener
import com.mcdev.spazes.model.FaveHost
import com.mcdev.spazes.repository.FirebaseEventListener
import com.mcdev.spazes.viewmodel.SpacesViewModel
import com.mcdev.twitterapikit.`object`.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@AndroidEntryPoint
class UsersActivity : AppCompatActivity(), UserAdapter.OnUserItemClickListener {
    private lateinit var binding: ActivityUsersBinding
    private val viewModel: SpacesViewModel by viewModels()
    private var userAdapter: UserAdapter? = null
    private var userId: String? = null
    private var ids : String? = null

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

        getFaveHosts(userId!!)

        binding.swipeRefresh.setOnRefreshListener {
            getFaveHosts(userId!!)
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
                        stopLoading(binding.swipeRefresh, binding.recyclerMessage, binding.usersRecyclerView)
                    }
                    is UserSingleEventListener.Failure -> {
                        stopLoading(binding.swipeRefresh, binding.recyclerMessage, binding.usersRecyclerView)
                        Toast.makeText(this@UsersActivity, "Failed.", Toast.LENGTH_SHORT).show()
                        Log.d("TAG", "onCreate: UserSingle Event listener is failed")
                    }
                    is UserSingleEventListener.Empty -> {
                        stopLoading(binding.swipeRefresh, binding.recyclerMessage, binding.usersRecyclerView)
                        showEmpty(binding.swipeRefresh, binding.recyclerMessage, binding.usersRecyclerView, R.string.no_hosts_found)
                        Log.d("TAG", "onCreate: UserSingle Event listener is empty")
                    }
                    is UserSingleEventListener.Loading -> {
                        startLoading(binding.swipeRefresh, binding.recyclerMessage)
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
                        stopLoading(binding.swipeRefresh, binding.recyclerMessage, binding.usersRecyclerView)
                    }
                    is UserListEventListener.Failure -> {
                        stopLoading(binding.swipeRefresh, binding.recyclerMessage, binding.usersRecyclerView)
                        Toast.makeText(this@UsersActivity, "Failed.", Toast.LENGTH_SHORT).show()
                        Log.d("TAG", "onCreate: UserList Event listener is failed")
                    }
                    is UserListEventListener.Empty -> {
                        stopLoading(binding.swipeRefresh, binding.recyclerMessage, binding.usersRecyclerView)
                        showEmpty(binding.swipeRefresh, binding.recyclerMessage, binding.usersRecyclerView, R.string.no_hosts_found)
                        Log.d("TAG", "onCreate: UserList Event listener is empty")
                    }
                    is UserListEventListener.Loading -> {
                        startLoading(binding.swipeRefresh, binding.recyclerMessage)
                        Log.d("TAG", "onCreate: UserList Event listener is loading")
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.fireStoreListener.collect{
                when (it) {
                    is FirebaseEventListener.DocumentSuccess -> {
                        stopLoading(binding.swipeRefresh, binding.recyclerMessage, binding.usersRecyclerView)

                        val hostIds: ArrayList<HashMap<String, String>> = it.data?.get("fave_hosts") as ArrayList<HashMap<String, String>>
                        ids = getTheIDs(hostIds)
                        //if the id list is empty or null, just display the empty message, otherwise you will be making query to the API with no ID at all which will throw an error

                        if (ids.isNullOrBlank()) {
                            showEmpty(binding.swipeRefresh, binding.recyclerMessage, binding.usersRecyclerView, R.string.no_hosts_found)
                        } else {
                            getUsersByIds(ids!!)
                        }
                    }
                    is FirebaseEventListener.Failure -> {
                        stopLoading(binding.swipeRefresh, binding.recyclerMessage, binding.usersRecyclerView)
                        Toast.makeText(this@UsersActivity, "Failed.", Toast.LENGTH_SHORT).show()
                    }
                    is FirebaseEventListener.Empty -> {
                        stopLoading(binding.swipeRefresh, binding.recyclerMessage, binding.usersRecyclerView)
                        showEmpty(binding.swipeRefresh, binding.recyclerMessage, binding.usersRecyclerView, it.message!!)
                    }
                    is FirebaseEventListener.Loading -> {
                        startLoading(binding.swipeRefresh, binding.recyclerMessage)
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
                    getUserByUsername(query!!.trim())

                    lifecycleScope.launchWhenStarted {
                        viewModel.findUserByUsername.collect {
                            when (it) {
                                is UserSingleEventListener.Success -> {
                                    val user: User = it.data?.data!!
                                    Log.d(
                                        "TAG",
                                        "onCreate: UserSingle Event listener is success : $user"
                                    )
                                    bottomSheetBinding.userDetails.itemLay.background = ResourcesCompat.getDrawable(resources, R.drawable.bg_users_add, applicationContext.theme)
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

    private fun getFaveHosts(userId: String) {
        viewModel.getFaveHosts(userId)
    }

    private fun getUsersByIds(ids: String) {
        viewModel.getUsersByIds(ids = ids)
    }

    private fun getTheIDs(result: ArrayList<HashMap<String, String>>): String{
        val theIDs = mutableListOf<String>()
        for (document in result) {
            theIDs.add(document["hostId"]!!)
        }
        //joinToString method will put them in a string and separator will separate without whitespaces
        return  theIDs.joinToString(separator = ",")
    }

    override fun onUserItemClick(user: User, position: Int) {
//        TODO("Not yet implemented")
    }

    override fun onAddRemoveItemClick(user: User, position: Int) {
        viewModel.removeFaveHost(userId!!, FaveHost(user.id))
    }

    override fun onResume() {
        super.onResume()
        if (ids != null) {
            getUsersByIds(ids = ids!!)
        }
    }
}