package com.mcdev.spazes.viewmodel

import android.preference.Preference
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class DatastoreViewModel @Inject constructor(private val datastore: DataStore<Preferences>) : ViewModel(){

    suspend fun saveOrUpdateDatastore(key: String, value: String) {
//        viewModelScope.launch {
        val dataStoreKey = stringPreferencesKey(key)
        datastore.edit {
            it[dataStoreKey] = value
        }
//        }
    }

    suspend fun updateAppIntroDatastore(key: String, value: Boolean) {
        val dataStoreKey = booleanPreferencesKey(key)
        datastore.edit {
            it[dataStoreKey] = value
        }
    }

    suspend fun readAppIntroDatastore(key: String): Boolean? {
        var value: Boolean? = null
        val dataStoreKey = booleanPreferencesKey(key)
        value = datastore.data.first()[dataStoreKey]
        val s = value
        return value
    }

    suspend fun readDatastore(key: String): String? {
        var value: String? = null
//        viewModelScope.launch(dispatchProvider.main) {
        val dataStoreKey = stringPreferencesKey(key)
        value = datastore.data.first()[dataStoreKey]
//        }
        val s = value
        return value
    }
}