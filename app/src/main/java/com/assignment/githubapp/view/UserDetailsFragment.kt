package com.assignment.githubapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.navArgs
import coil.api.load
import com.assignment.githubapp.R
import com.assignment.githubapp.databinding.FragmentUserDetailsBinding
import com.assignment.githubapp.model.ApiResult
import com.assignment.githubapp.model.NetworkStatusState
import com.assignment.githubapp.model.RemoteUserDetails
import com.assignment.githubapp.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

/**
 * Github user profile details fragment of the application, which display the profile details like name, image
 */

@AndroidEntryPoint
class UserDetailsFragment : Fragment() {

    private var binding: FragmentUserDetailsBinding? = null
    private val arguments: UserDetailsFragmentArgs by navArgs()
    private val viewModel by viewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserDetailsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        fetchData()

    }

    private fun setupObservers() {
        // listener for mobile network connectivity changes
        viewModel.networkState.asLiveData().observe(this) { state ->
            when (state) {
                NetworkStatusState.NetworkStatusConnected -> {
                    fetchData()
                }
                else -> {
                    context?.let {
                        displayError(getString(R.string.err_network))
                    }
                }
            }
        }

        viewModel.userDetails.observe(this) {
            it?.let {
                binding?.progressBar?.isVisible = it is ApiResult.Loading
                binding?.textViewError?.isVisible = it is ApiResult.Error

                when (it) {
                    is ApiResult.Success -> displayDetails(it.data)
                    else -> {}
                }
            }
        }
    }

    private fun fetchData() {
        if(!viewModel.isDeviceOnline()) {
            displayError(getString(R.string.err_network))
        }
        else {
            viewModel.getUserDetails(arguments.user)
        }
    }

    // display the details like name, image
    private fun displayDetails(userDetails : RemoteUserDetails) {
        userDetails.let { userDetails ->
            binding?.apply {
                userDetails.imageUrl?.let {
                    imgViewUser.isVisible = true
                    imgViewUser.load(it) {
                        crossfade(true)
                        placeholder(R.drawable.ic_loading)
                        error(R.drawable.ic_error)
                    }
                }

                userDetails.loginId?.let {
                    txtLoginId.isVisible = true
                    txtLoginId.text = it
                }

                userDetails.name?.let {
                    txtTitle.isVisible = true
                    txtTitle.text = it
                }

                userDetails.bio?.let {
                    txtBio.isVisible = true
                    txtBio.text = it
                }

                userDetails.company?.let {
                    txtCompany.isVisible = true
                    txtCompany.text = it
                }

                userDetails.location?.let {
                    txtLocation.isVisible = true
                    txtLocation.text = it
                }

                userDetails.followers.let {
                    txtFollowers.isVisible = true
                    txtFollowers.text = getString(R.string.followers, it)
                }
            }
        }
    }

    private fun displayError(message: String) {
        binding?.apply {
            textViewError.isVisible = true
            root.let {
                Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}