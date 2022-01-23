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
    private val mutableStateFlow = MutableStateFlow<SpacesEventListener>(SpacesEventListener.Empty)
    val search: StateFlow<SpacesEventListener> = mutableStateFlow


    fun searchSpaces(token: String, query: String, spaceFields: String, userFields: String, expansions: String){

        viewModelScope.launch(dispatchProvider.io) {
            mutableStateFlow.value = SpacesEventListener.Loading
            when(val spacesResponse = mainRepository.getSpacesByTitle(token, query, spaceFields, userFields, expansions)) {
                is Resource.Success -> {
                    val spaces = spacesResponse.data

                    if (spaces?.meta?.resultCount == 0) {
                        mutableStateFlow.value =
                            SpacesEventListener.Empty
                    } else {
                        mutableStateFlow.value = SpacesEventListener.Success(spaces)
                    }
                }
                is Resource.Error -> {
                    mutableStateFlow.value = SpacesEventListener.Failure(spacesResponse.data?.detail)
                }
                else -> {
                    mutableStateFlow.value = SpacesEventListener.Failure(spacesResponse.error)
                }
            }
        }
    }
}