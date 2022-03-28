package com.assignment.githubapp.model

sealed class ApiResult<Value:Any>{

    data class Loading<Value:Any>(val data:Value): ApiResult<Value>()
    data class Success<Value:Any>(val data:Value): ApiResult<Value>()
    data class Error<Value:Any>(val exception:Throwable): ApiResult<Value>()

}