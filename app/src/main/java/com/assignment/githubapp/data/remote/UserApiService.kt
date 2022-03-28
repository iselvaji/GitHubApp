package com.assignment.githubapp.data.remote

import com.assignment.githubapp.model.RemoteUserDetails
import com.assignment.githubapp.model.RemoteUserResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API Configuration to fetch git hub users profiles via Retrofit.
 */
interface UserApiService {

    //@GET("search/users?sort=followers")
    @GET("search/users?")
    suspend fun searchUser(
       @Query("q") query: String,
       @Query("page") page: Int,
       @Query("per_page") itemsPerPage: Int) : RemoteUserResponse

    @GET("users/{user}")
    suspend fun getUserDetail(
        @Path("user") id: String
    ): RemoteUserDetails
}