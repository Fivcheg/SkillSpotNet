package ru.netology.skillspotnet.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.skillspotnet.auth.AppAuth
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiServiceModule {
    @Provides
    @Singleton
    fun provideApiService(auth: AppAuth): ApiService {
        return retrofit(okhttp(authInterceptor(auth), loggingInterceptor()))
            .create(ApiService::class.java)
    }
}