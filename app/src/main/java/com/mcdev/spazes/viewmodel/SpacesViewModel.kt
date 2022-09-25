package com.mcdev.spazes.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mcdev.spazes.R
import com.mcdev.spazes.events.SpacesListEventListener
import com.mcdev.spazes.events.SpacesSingleEventListener
import com.mcdev.spazes.events.UserListEventListener
import com.mcdev.spazes.events.UserSingleEventListener
import com.mcdev.spazes.model.FaveHost
import com.mcdev.spazes.repository.FirebaseEventListener
import com.mcdev.spazes.repository.MainRepository
import com.mcdev.spazes.repository.UsersMainRepository
import com.mcdev.spazes.util.BEARER_TOKEN
import com.mcdev.spazes.util.DBCollections
import com.mcdev.spazes.util.DispatchProvider
import com.mcdev.spazes.util.Resource
import com.mcdev.twitterapikit.`object`.Space
import com.mcdev.twitterapikit.expansion.SpacesExpansion
import com.mcdev.twitterapikit.expansion.UsersExpansion
import com.mcdev.twitterapikit.field.SpaceField
import com.mcdev.twitterapikit.field.TopicField
import com.mcdev.twitterapikit.field.TweetField
import com.mcdev.twitterapikit.field.UserField
import com.mcdev.twitterapikit.response.SpaceListResponse
import com.mcdev.twitterapikit.response.UserSingleResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class SpacesViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val usersMainRepository: UsersMainRepository,
    private val dispatchProvider: DispatchProvider,
    private val fireStore: FirebaseFirestore
) : ViewModel() {

    /*using state flow*/
    private val mutableListStateFlow =
        MutableStateFlow<SpacesListEventListener>(SpacesListEventListener.Loading)
    private val mutableSingleStateFlow =
        MutableStateFlow<SpacesSingleEventListener>(SpacesSingleEventListener.Loading)
    private val mutableFirebaseStateFlow =
        MutableStateFlow<FirebaseEventListener>(FirebaseEventListener.Loading)
    private val mutableListUserStateFlow =
        MutableStateFlow<UserListEventListener>(UserListEventListener.Loading)
    private val mutableSingleUserStateFlow =
        MutableStateFlow<UserSingleEventListener>(UserSingleEventListener.Loading)

    val search: StateFlow<SpacesListEventListener> = mutableListStateFlow
    val fireStoreListener: StateFlow<FirebaseEventListener> = mutableFirebaseStateFlow
    val findUserByUsername : StateFlow<UserSingleEventListener> = mutableSingleUserStateFlow
    val findUserById : StateFlow<UserSingleEventListener> = mutableSingleUserStateFlow
    val findUsersByUserNames: StateFlow<UserListEventListener> = mutableListUserStateFlow

    /*search spaces by query*/
    fun searchSpaces(
        token: String,
        query: String,
        spaceFields: String,
        userFields: String,
        expansions: String,
        topicFields: String
    ) {

        viewModelScope.launch(dispatchProvider.io) {
            mutableListStateFlow.value = SpacesListEventListener.Loading
            when (val spacesListResponse = mainRepository.getSpacesByTitle(
                token,
                query,
                spaceFields,
                userFields,
                expansions,
                topicFields
            )) {
                is Resource.Success -> {
                    val spaces = spacesListResponse.data

                    if (spaces?.meta?.resultCount == 0) {
                        mutableListStateFlow.value =
                            SpacesListEventListener.Empty(R.string.no_spaces_found)
                    } else {
                        mutableListStateFlow.value = SpacesListEventListener.Success(spaces)
                    }
                }
                is Resource.Error -> {
                    mutableListStateFlow.value =
                        SpacesListEventListener.Failure(spacesListResponse.data?.detail)
                }
                else -> {
                    mutableListStateFlow.value =
                        SpacesListEventListener.Failure(spacesListResponse.error)
                }
            }
        }
    }


    /*search spaces by list of ids*/
    fun searchSpacesByIds(
        token: String,
        ids: String,
        spaceFields: String,
        userFields: String,
        expansions: String,
        topicFields: String,
        firestoreCollection: String
    ) {

        viewModelScope.launch(dispatchProvider.io) {
            mutableListStateFlow.value = SpacesListEventListener.Loading

            when (val spacesListResponse = mainRepository.getSpacesByIds(
                token,
                ids,
                spaceFields,
                userFields,
                expansions,
                topicFields
            )) {
                is Resource.Success -> {
                    val spaces = spacesListResponse.data

                    if (spaces?.meta?.resultCount == 0) {
                        mutableListStateFlow.value =
                            SpacesListEventListener.Empty(R.string.no_featured_spaces)
                    } else {
                        val validFeatured = isSpaceExpired(
                            spaceResponseList = spaces,
                            firestoreCollection = firestoreCollection
                        )
                        if (validFeatured.data.isNullOrEmpty()) {
                            mutableListStateFlow.value =
                                SpacesListEventListener.Empty(R.string.no_featured_spaces)
                        } else {
                            mutableListStateFlow.value =
                                SpacesListEventListener.Success(validFeatured)
                        }
                    }
                }
                is Resource.Error -> {
                    mutableListStateFlow.value =
                        SpacesListEventListener.Failure(spacesListResponse.data?.detail)
                }
                else -> {
                    mutableListStateFlow.value =
                        SpacesListEventListener.Failure(spacesListResponse.error)
                }
            }
        }
    }

    /*search spaces by id*/
    fun searchSpacesById(
        token: String,
        id: String,
        spaceFields: String,
        userFields: String,
        expansions: String,
        topicFields: String
    ) {
        viewModelScope.launch(dispatchProvider.io) {
            mutableSingleStateFlow.value = SpacesSingleEventListener.Loading
            when (val spacesSingleResponse = mainRepository.getSpacesById(
                token, id, spaceFields, userFields, expansions, topicFields
            )) {
                is Resource.Success -> {
                    val space = spacesSingleResponse.data

                    if (space?.meta?.resultCount == 0) {
                        mutableSingleStateFlow.value = SpacesSingleEventListener.Empty
                    } else {
                        mutableSingleStateFlow.value = SpacesSingleEventListener.Success(space)
                    }
                }

                is Resource.Error -> {
                    mutableSingleStateFlow.value =
                        SpacesSingleEventListener.Failure(spacesSingleResponse.data?.detail)
                }
                else -> {
                    mutableSingleStateFlow.value =
                        SpacesSingleEventListener.Failure(spacesSingleResponse.error)
                }

            }
        }
    }

    /*get spaces by creator ids*/
    fun searchSpacesByCreatorIds(
        token: String = "BEARER $BEARER_TOKEN",
        ids: String,
        spaceFields: String = SpaceField.ALL.value,
        userFields: String = "created_at,description,entities,id,location,name,pinned_tweet_id,profile_image_url,protected,public_metrics,url,username,verified,withheld",
        expansions: String = "invited_user_ids,speaker_ids,creator_id,host_ids",
        topicFields: String = "description,id,name"
    ) {
       viewModelScope.launch(dispatchProvider.io) {
           mutableListStateFlow.value = SpacesListEventListener.Loading
           when (val spacesListResponse = mainRepository.getSpacesByCreatorIds(
               token,
               ids,
               spaceFields,
               userFields,
               expansions,
               topicFields
           )) {
               is Resource.Success -> {
                   val spaces = spacesListResponse.data

                   if (spaces?.meta?.resultCount == 0) {
                       mutableListStateFlow.value =
                           SpacesListEventListener.Empty(R.string.no_spaces_found)
                   } else {
                       mutableListStateFlow.value = SpacesListEventListener.Success(spaces)
                   }
               }
               is Resource.Error -> {
                   mutableListStateFlow.value =
                       SpacesListEventListener.Failure(spacesListResponse.data?.detail)
               }
               else -> {
                   mutableListStateFlow.value =
                       SpacesListEventListener.Failure(spacesListResponse.error)
               }
           }
       }
    }

    fun getUsersByUsername(
        token: String = "BEARER $BEARER_TOKEN",
        username: String,
        expansions: String = UsersExpansion.PINNED_TWEET_ID.value,
        tweetFields: String = TweetField.ALL_DEFAULT.value,
        userFields: String = "created_at,description,entities,id,location,name,pinned_tweet_id,profile_image_url,protected,public_metrics,url,username,verified,withheld"
        ) {
        viewModelScope.launch(dispatchProvider.io) {
            mutableListUserStateFlow.value = UserListEventListener.Loading
            when (val userSingleResponse = usersMainRepository.getUsersByUsername(
                token,
                username,
                expansions,
                tweetFields,
                userFields
            )) {
                is Resource.Success -> {
                    val user = userSingleResponse.data

                    if (user?.data != null) {
                        mutableSingleUserStateFlow.value = UserSingleEventListener.Success(user)
                    } else {
                        mutableSingleUserStateFlow.value = UserSingleEventListener.Empty(R.string.no_hosts_found)
                    }
                }
                is Resource.Error -> {
                    mutableSingleUserStateFlow.value =
                        UserSingleEventListener.Failure(userSingleResponse.data?.detail)
                }
                else -> {
                    mutableSingleUserStateFlow.value =
                        UserSingleEventListener.Failure(userSingleResponse.error)
                }
            }
        }

    }

    fun getUsersByUsernames(
        token: String = "BEARER $BEARER_TOKEN",
        usernames: String,
        expansions: String = UsersExpansion.PINNED_TWEET_ID.value,
        tweetFields: String = TweetField.ALL_DEFAULT.value,
        userFields: String = "created_at,description,entities,id,location,name,pinned_tweet_id,profile_image_url,protected,public_metrics,url,username,verified,withheld"
    ) {
        viewModelScope.launch(dispatchProvider.io) {
            mutableListUserStateFlow.value = UserListEventListener.Loading
            when (val userListResponse = usersMainRepository.getUsersByUsernames(
                token,
                usernames,
                expansions,
                tweetFields,
                userFields
            )) {
                is Resource.Success -> {
                    val users = userListResponse.data

                    if (users?.data != null) {
                        mutableListUserStateFlow.value = UserListEventListener.Success(users)
                    } else {
                        mutableListUserStateFlow.value = UserListEventListener.Empty(R.string.no_hosts_found)
                    }
                }
                is Resource.Error -> {
                    mutableListUserStateFlow.value =
                        UserListEventListener.Failure(userListResponse.data?.detail)
                }
                else -> {
                    mutableListUserStateFlow.value =
                        UserListEventListener.Failure(userListResponse.error)
                }
            }
        }
    }


    fun getUsersByIds(
        token: String = "BEARER $BEARER_TOKEN",
        ids: String,
        expansions: String = UsersExpansion.PINNED_TWEET_ID.value,
        tweetFields: String = TweetField.ALL_DEFAULT.value,
        userFields: String = "created_at,description,entities,id,location,name,pinned_tweet_id,profile_image_url,protected,public_metrics,url,username,verified,withheld"
    ) {
        viewModelScope.launch(dispatchProvider.io) {
            mutableListUserStateFlow.value = UserListEventListener.Loading
            when (val userListResponse = usersMainRepository.getUsersByIds(
                token,
                ids,
                expansions,
                tweetFields,
                userFields
            )) {
                is Resource.Success -> {
                    val users = userListResponse.data

                    if (users?.data != null) {
                        mutableListUserStateFlow.value = UserListEventListener.Success(users)
                    } else {
                        mutableListUserStateFlow.value = UserListEventListener.Empty(R.string.no_hosts_found)
                    }
                }
                is Resource.Error -> {
                    mutableListUserStateFlow.value =
                        UserListEventListener.Failure(userListResponse.data?.detail)
                }
                else -> {
                    mutableListUserStateFlow.value =
                        UserListEventListener.Failure(userListResponse.error)
                }
            }
        }
    }

    fun getUserById(
        token: String = "BEARER $BEARER_TOKEN",
        id: String,
        expansions: String = UsersExpansion.PINNED_TWEET_ID.value,
        tweetFields: String = TweetField.ALL_DEFAULT.value,
        userFields: String = "created_at,description,entities,id,location,name,pinned_tweet_id,profile_image_url,protected,public_metrics,url,username,verified,withheld"
    ) {
        viewModelScope.launch(dispatchProvider.io) {
            mutableSingleUserStateFlow.value = UserSingleEventListener.Loading
            when (val userSingleResponse = usersMainRepository.getUserById(
                token,
                id,
                expansions,
                tweetFields,
                userFields
            )) {
                is Resource.Success -> {
                    val user = userSingleResponse.data

                    if (user?.data != null) {
                        mutableSingleUserStateFlow.value = UserSingleEventListener.Success(user)
                    } else {
                        mutableSingleUserStateFlow.value = UserSingleEventListener.Empty(R.string.user_not_found)
                    }
                }
                is Resource.Error -> {
                    mutableSingleUserStateFlow.value =
                        UserSingleEventListener.Failure(userSingleResponse.data?.detail)
                }
                else -> {
                    mutableSingleUserStateFlow.value =
                        UserSingleEventListener.Failure(userSingleResponse.error)
                }
            }
        }
    }

    private fun isSpaceExpired(
        spaceResponseList: SpaceListResponse?,
        firestoreCollection: String
    ): SpaceListResponse {
        val validSpaces = ArrayList<Space>()
        if (spaceResponseList?.data != null) {
            for (space in spaceResponseList.data!!) {
                if (space.state.equals("ended").not()) {
                    validSpaces.add(space)
                } else {
                    //delete ended featured spaces. The delete option was chosen so that there will not be multiple reads to the database
                    fireStore.collection(firestoreCollection)
                        .document(space.id!!)
                        .delete()
                        //.update("is_ended", true)
                        .addOnSuccessListener {
                            Log.d("TAG", "isSpaceExpired: delete success for ${space.id}")
                        }
                        .addOnFailureListener {
                            Log.d("TAG", "isSpaceExpired: delete failure")
                        }
                }
            }
            Log.d("TAG", "valid spaces are : $validSpaces")
        }

        spaceResponseList?.data = validSpaces
        return spaceResponseList!!
    }



    fun getFeaturedSpaces() {
        viewModelScope.launch(dispatchProvider.io) {
            mutableFirebaseStateFlow.value = FirebaseEventListener.Loading
            getSpacesIds(DBCollections.Featured)
        }
    }

    fun getTrendingSpaces() {
        viewModelScope.launch(dispatchProvider.io) {
            mutableFirebaseStateFlow.value = FirebaseEventListener.Loading
            getSpacesIds(DBCollections.Trending)
        }
    }

    fun addUser(documentName: String, data: HashMap<String, *>) {
        viewModelScope.launch {
            mutableFirebaseStateFlow.value = FirebaseEventListener.Loading
            addData(DBCollections.Users, documentName, data)
        }
    }

    fun addUsers(documentName: String, data: Any) {
        viewModelScope.launch {
            mutableFirebaseStateFlow.value = FirebaseEventListener.Loading
            addData(DBCollections.Users, documentName, data)
        }
    }

    fun removeFaveHost(documentName: String, data: Any) {
        viewModelScope.launch {
            mutableFirebaseStateFlow.value = FirebaseEventListener.Loading
            removeHost(DBCollections.Users, documentName, data)
        }
    }

    private fun getSpacesIds(dbCollections: DBCollections) {
        fireStore.collection(dbCollections.toString())
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    mutableFirebaseStateFlow.value =
                        FirebaseEventListener.Empty(R.string.no_trending_space)
                } else {
                    mutableFirebaseStateFlow.value = FirebaseEventListener.Success(it)
                }
            }
            .addOnFailureListener {
                mutableFirebaseStateFlow.value =
                    FirebaseEventListener.Failure("An error occurred getting featured spaces")
            }
    }

    fun getFaveHosts(documentName: String) {
        viewModelScope.launch {
            mutableFirebaseStateFlow.value = FirebaseEventListener.Loading
            getFaveHostsIds(DBCollections.Users, documentName)
        }
    }

    private fun getFaveHostsIds(dbCollections: DBCollections = DBCollections.Users, document: String) {
        fireStore.collection(dbCollections.toString())
            .document(document)
            .get()
            .addOnSuccessListener {
                if (it.get("fave_hosts") == null) {
                    mutableFirebaseStateFlow.value =
                        FirebaseEventListener.Empty(R.string.no_hosts_found)
                } else {
                    mutableFirebaseStateFlow.value = FirebaseEventListener.DocumentSuccess(it)
                }
            }
            .addOnFailureListener {
                mutableFirebaseStateFlow.value =
                    FirebaseEventListener.Failure("An error occurred getting featured spaces")
            }
    }

    private fun addData(
        dbCollections: DBCollections,
        documentName: String,
        data: HashMap<String, *>
    ) {
        fireStore.collection(dbCollections.toString())
            .document(documentName)
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                mutableFirebaseStateFlow.value = FirebaseEventListener.Success()
            }
            .addOnFailureListener {
                mutableFirebaseStateFlow.value =
                    FirebaseEventListener.Failure("An Error occurred adding user")
            }
    }

    private fun addData(
        dbCollections: DBCollections,
        documentName: String,
        data: Any
    ) {
        fireStore.collection(dbCollections.toString())
            .document(documentName)
            .update("fave_hosts", FieldValue.arrayUnion(data))
            .addOnSuccessListener {
                mutableFirebaseStateFlow.value = FirebaseEventListener.UserAddedSuccess
                //when a host is added, fetch the remaining hosts
                getFaveHosts(documentName)
            }
            .addOnFailureListener {
                mutableFirebaseStateFlow.value =
                    FirebaseEventListener.Failure("An Error occurred adding user")
            }
    }

    private fun removeHost(
        dbCollections: DBCollections,
        documentName: String,
        data: Any
    ) {
        fireStore.collection(dbCollections.toString())
            .document(documentName)
            .update("fave_hosts", FieldValue.arrayRemove(data))
            .addOnSuccessListener {
                //when a host is deleted pr removed, fetch the remaining hosts
                getFaveHosts(documentName)
            }
            .addOnFailureListener {
                mutableFirebaseStateFlow.value =
                    FirebaseEventListener.Failure("An Error occurred adding user")
            }
    }
}