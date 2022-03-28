package com.assignment.githubapp.model

import android.os.Parcelable
import com.squareup.moshi.Json

@kotlinx.parcelize.Parcelize
data class RemoteUserResponse (

    @Json(name = "total_count")
    val total_count : Int,

    @Json(name = "incomplete_results")
    val incomplete_results : Boolean,

    @Json(name = "items")
    val items : List<RemoteUser>

) : Parcelable