package com.assignment.githubapp.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.assignment.githubapp.model.RemoteUser
import com.assignment.githubapp.model.RemoteUserResponse

class UserPagingSource(private val dataSource: RemoteDataSource, private val query : String) : PagingSource<Int, RemoteUser>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RemoteUser> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response: RemoteUserResponse = dataSource.searchUser(query, nextPageNumber, params.loadSize)
            LoadResult.Page(
                data = response.items,
                prevKey = if (nextPageNumber > 0) nextPageNumber - 1 else null,
                nextKey = if (nextPageNumber < response.total_count) nextPageNumber + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RemoteUser>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}