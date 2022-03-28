package com.assignment.githubapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.assignment.githubapp.data.remote.RemoteDataSource
import com.assignment.githubapp.data.remote.UserPagingSource
import com.assignment.githubapp.model.ApiResult
import com.assignment.githubapp.model.RemoteUser
import com.assignment.githubapp.model.RemoteUserDetails
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation that uses [androidx.paging.PagingSource]
 * to load pages from network when there are no more items cached
 */

@Singleton
class UserRepository @Inject constructor(private val remoteDataSource: RemoteDataSource,
                                         private val dispatcher: CoroutineDispatcher) {

    @OptIn(ExperimentalPagingApi::class)
    fun searchUsers(query: String): Flow<PagingData<RemoteUser>> {
        return Pager(
            config = PagingConfig(PAGE_SIZE, enablePlaceholders = true, prefetchDistance = PRE_FETCH_DISTANCE),
            pagingSourceFactory = { UserPagingSource(remoteDataSource, query) }
        ).flow
    }

    suspend fun getUserDetails(userName: String): ApiResult<RemoteUserDetails> =
         withContext(dispatcher) {
         try {
             ApiResult.Loading("")
             val data = remoteDataSource.getUserDetails(userName)
             ApiResult.Success(data)
         } catch (ex: Exception) {
             ApiResult.Error(ex)
         }
     }

    companion object {
        const val PAGE_SIZE = 10
        const val PRE_FETCH_DISTANCE = 2
    }
}