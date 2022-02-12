package com.mcdev.spazes.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.mcdev.spazes.repository.MainRepository
import com.mcdev.spazes.repository.SpacesRepository
import com.mcdev.spazes.service.SpacesApiService
import com.mcdev.spazes.util.DispatchProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val TWITTER_API_BASE_URL = "https://api.twitter.com"
    private const val URL_PREFERENCE_NAME = "url_preferences"

    /*Retrofit Spaces API*/
    @Singleton
    @Provides
    fun provideSpacesApi(): SpacesApiService = Retrofit.Builder()
        .baseUrl(TWITTER_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SpacesApiService::class.java)


    /*@Single*/
    @Singleton
    @Provides
    fun provideDispatchers(): DispatchProvider = object : DispatchProvider {
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }

    @Singleton
    @Provides
    fun provideMainRepository(spacesApiService: SpacesApiService): MainRepository =
        SpacesRepository(spacesApiService)

    /*Datastore preference*/
    @Singleton
    @Provides
    fun providePreferencesDatastore(@ApplicationContext applicationContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler( produceNewData = { emptyPreferences() }),//is invoked if a corruption exception is thrown by the serializer when the data cannot be deserialized which instructs datastore how to replace the corrupted data
            migrations = listOf(SharedPreferencesMigration(applicationContext, URL_PREFERENCE_NAME)),//migrations is a list of data migrations for moving previous data into datastore
            scope = CoroutineScope(provideDispatchers().io + SupervisorJob()),//defines the scope in which IO operations and data editing functions will execute
            produceFile = { applicationContext.preferencesDataStoreFile(URL_PREFERENCE_NAME) }//generates the file object for datastore based on the provided context name

        )
    }
}