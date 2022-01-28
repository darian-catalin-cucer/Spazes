package com.mcdev.spazes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcdev.spazes.repository.MainRepository
import com.mcdev.spazes.util.DispatchProvider
import com.mcdev.spazes.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpacesViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val dispatchProvider: DispatchProvider
) : ViewModel() {

    /*using state flow*/
    private val mutableListStateFlow = MutableStateFlow<SpacesListEventListener>(SpacesListEventListener.Empty)
    private val mutableSingleStateFlow = MutableStateFlow<SpacesSingleEventListener>(SpacesSingleEventListener.Empty)

    val search: StateFlow<SpacesListEventListener> = mutableListStateFlow


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
                            SpacesListEventListener.Empty
                    } else {
                        mutableListStateFlow.value = SpacesListEventListener.Success(spaces)
                    }
                }
                is Resource.Error -> {
                    mutableListStateFlow.value =
                        SpacesListEventListener.Failure(spacesListResponse.data?.detail)
                }
                else -> {
                    mutableListStateFlow.value = SpacesListEventListener.Failure(spacesListResponse.error)
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
        topicFields: String
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
                            SpacesListEventListener.Empty
                    } else {
                        mutableListStateFlow.value = SpacesListEventListener.Success(spaces)
                    }
                }
                is Resource.Error -> {
                    mutableListStateFlow.value =
                        SpacesListEventListener.Failure(spacesListResponse.data?.detail)
                }
                else -> {
                    mutableListStateFlow.value = SpacesListEventListener.Failure(spacesListResponse.error)
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
                    mutableSingleStateFlow.value = SpacesSingleEventListener.Failure(spacesSingleResponse.error)
                }

            }
        }
    }


}