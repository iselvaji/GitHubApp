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
data class RemoteUserDetails (

	@Json(name = "id")
	val id : String?,

	@Json(name = "login")
	val loginId : String?,

	@Json(name = "avatar_url")
	val imageUrl : String?,

	@Json(name = "name")
	val name : String?,

	@Json(name = "company")
	val company : String?,

	@Json(name = "location")
	val location : String?,

	@Json(name = "bio")
	val bio : String?,

	@Json(name = "followers")
	val followers : Int = 0,

) : Parcelable