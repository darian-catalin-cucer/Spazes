package com.mcdev.spazes.di

import com.mcdev.spazes.repository.MainRepository
import com.mcdev.spazes.repository.SpacesRepository
import com.mcdev.spazes.service.SpacesApiService
import com.mcdev.spazes.util.DispatchProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val TWITTER_API_BASE_URL = "https://api.twitter.com"

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
    fun provideMainRepository(spacesApiService: SpacesApiService): MainRepository = SpacesRepository(spacesApiService)
}