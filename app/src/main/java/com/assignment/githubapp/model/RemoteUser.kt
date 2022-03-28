package com.assignment.githubapp.model

import android.os.Parcelable
import com.squareup.moshi.Json

/**
 * Model class for a Github repo that holds all the information about github user.
 * Objects of this type are received from the API, so all the fields are annotated
 * with the serialized name.
 * Its a parcelable class which can be shared between UI fragments or activities
 */

@kotlinx.parcelize.Parcelize
data class RemoteUser (

	@Json(name = "id")
	val id : String = "",

	@Json(name = "login")
	val loginId : String = "",

	@Json(name = "avatar_url")
	val imageUrl : String = ""

) : Parcelable