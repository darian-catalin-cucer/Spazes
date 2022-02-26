package com.mcdev.spazes.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.mcdev.spazes.util.DispatchProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class FirebaseViewModel @Inject constructor(
    private val dispatchProvider: DispatchProvider
//    private val fireStore: FirebaseFirestore
): ViewModel(){

//    private val mutableListStateFlow = MutableStateFlow<>
}