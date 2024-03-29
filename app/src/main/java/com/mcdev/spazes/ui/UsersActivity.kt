package com.mcdev.spazes.ui

import android.graphics.Typeface
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
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeActivity
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
import com.mcdev.spazes.theme.BaseTheme
import com.mcdev.spazes.theme.DarkTheme
import com.mcdev.spazes.theme.DefaultTheme
import com.mcdev.spazes.theme.LightTheme
import com.mcdev.spazes.viewmodel.DatastoreViewModel
import com.mcdev.spazes.viewmodel.SpacesViewModel
import com.mcdev.twitterapikit.`object`.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import spencerstudios.com.bungeelib.Bungee
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@AndroidEntryPoint
class UsersActivity : ThemeActivity(), UserAdapter.OnUserItemClickListener {
    private lateinit var binding: ActivityUsersBinding
    private val viewModel: SpacesViewModel by viewModels()
    private val dataStoreViewModel: DatastoreViewModel by viewModels()
    private var themeMode : AppTheme = DefaultTheme()
    private var userAdapter: UserAdapter? = null
    private var userId: String? = null
    private var ids : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        changeStatusBarColor(R.color.white)
        binding.recyclerMessage.lottieRawResource = R.raw.hosts // setting lottie file for empty state

        val loadAction = intent.extras?.get("loadAction") as LoadAction
        val userTwitterId = intent.extras?.get("user_twitter_id").toString()
        userId = intent.extras?.get("user_firebase_id").toString()

        userAdapter = UserAdapter(this, this, themeMode)
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
            Bungee.slideRight(this)
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
                    is FirebaseEventListener.UserAddedSuccess -> {
                        Toast.makeText(this@UsersActivity, "Added", Toast.LENGTH_SHORT).show()
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
                    else -> {
                        Log.d("TAG", "onCreate: else branch called")
                    }
                }
            }
        }
    }

    override fun syncTheme(appTheme: AppTheme) {
        val tt = appTheme as BaseTheme
        changeStatusBarColor(tt.statusBarColor())
        binding.root.setBackgroundColor(tt.activityBgColor(this))
        binding.titleText.setTextColor(resources.getColor(tt.textColor(), this.theme))
        binding.recyclerMessage.theme = tt
    }

    private fun showBottomSheet() {
        val addHostBottomSheet = BottomSheetDialog(this)
        val bottomSheetBinding = BottomsheetLayoutBinding.inflate(layoutInflater)
        addHostBottomSheet.setContentView(bottomSheetBinding.root)
        addHostBottomSheet.show()


        bottomSheetBinding.bottomSheetSearch.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchQuery: String?): Boolean {
                var query = searchQuery
                if (query.isNullOrBlank().not()) {
                    if (query!!.startsWith("@"))
                        query = query.removePrefix("@")

                    getUserByUsername(query.trim())

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
                                    bottomSheetBinding.userDetails.userName.apply {
                                        username = user.username
                                    }
                                    bottomSheetBinding.userDetails.userDisplayName.apply {
                                        customizeDisplayName.apply {
                                            textSize = 17f
                                            setTypeface(this.typeface, Typeface.BOLD)
                                        }
                                        setDisplayName(user.name!!, user.verified)
                                    }

                                    bottomSheetBinding.progressAnimationView.visibility = View.GONE
                                    bottomSheetBinding.userDetails.root.visibility = View.VISIBLE

                                    //onClick listener
                                    bottomSheetBinding.userDetails.addRemoveBtn.setOnClickListener {
                                        val faveHost = FaveHost(user.id)
                                        val hashMap = hashMapOf("fave_hosts" to arrayListOf(faveHost))
                                        addFaveHost(userId!!, faveHost)

                                        //dismiss
                                        addHostBottomSheet.dismiss()
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        Bungee.slideRight(this)
    }
}