package com.assignment.githubapp

import com.assignment.githubapp.model.RemoteUser
import com.assignment.githubapp.model.RemoteUserDetails
import com.assignment.githubapp.model.RemoteUserResponse

object TestData {

    fun getRemoteListOfUsers(): RemoteUserResponse {
        return RemoteUserResponse(
            total_count = 2, incomplete_results = false, items = listOf(
                RemoteUser("1", "Test", ""),
                RemoteUser("2", "Selva", "")
            )
        )
    }

    fun getRemoteUserDetails(): RemoteUserDetails {
        return RemoteUserDetails("test",null,null,null,null,null,
            null,0)
    }

}