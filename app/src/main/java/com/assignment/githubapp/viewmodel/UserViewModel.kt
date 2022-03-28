package com.assignment.githubapp.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.assignment.githubapp.data.NetworkStatusRepository
import com.assignment.githubapp.data.UserRepository
import com.assignment.githubapp.model.ApiResult
import com.assignment.githubapp.model.NetworkStatusState
import com.assignment.githubapp.model.RemoteUserDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the UserListFragment screen.
 * The ViewModel works with the [UserRepository] to get the data from remote source
 */

@HiltViewModel
class UserViewModel @Inject constructor (
    private val networkStatusRepository: NetworkStatusRepository,
    private val repository: UserRepository,
    application: Application
) : AndroidViewModel(application) {

    val networkState: StateFlow<NetworkStatusState> = networkStatusRepository.state
    private val searchQuery = MutableLiveData<String>()
    val userDetails : MutableLiveData<ApiResult<RemoteUserDetails>> = MutableLiveData()

    fun isDeviceOnline() : Boolean = networkStatusRepository.hasNetworkConnection()

    val users = searchQuery.switchMap {
        repository.searchUsers(it).asLiveData().cachedIn(viewModelScope)
    }

    fun searchUsers(query: String) {
        searchQuery.value = query
    }

    fun getUserDetails(userName: String) {
        viewModelScope.launch {
            userDetails.value = repository.getUserDetails(userName)
        }
    }
}