package com.assignment.githubapp.di

import android.content.Context
import com.assignment.githubapp.data.NetworkStatusRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

/**
 * This module used dependency injection (Hilt) for Network connectivity change related classes
 */

@InstallIn(SingletonComponent::class)
@Module
object ConnectivityModule {

    @Singleton
    @Provides
    fun provideNetworkRepository(@ApplicationContext context: Context, dispatcher: CoroutineDispatcher, scope: CoroutineScope): NetworkStatusRepository {
        return NetworkStatusRepository(context, dispatcher, scope)
    }
}