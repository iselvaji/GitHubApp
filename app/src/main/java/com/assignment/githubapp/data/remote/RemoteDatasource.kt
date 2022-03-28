package com.assignment.githubapp.data.remote

import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val apiService: UserApiService) {

    suspend fun searchUser(query:String, position:Int, itemsPerPage:Int) = apiService.searchUser(query, position, itemsPerPage)

    suspend fun getUserDetails(userName: String) = apiService.getUserDetail(userName)
}
