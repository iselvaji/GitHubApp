package com.assignment.githubapp.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.assignment.githubapp.R
import com.assignment.githubapp.databinding.FragmentUserListBinding
import com.assignment.githubapp.model.NetworkStatusState
import com.assignment.githubapp.model.RemoteUser
import com.assignment.githubapp.view.adapter.UserListAdapter
import com.assignment.githubapp.view.adapter.UserLoadStateAdapter
import com.assignment.githubapp.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Github user list fragment of the application, user can search and load the matched git hub users profile
 */

@AndroidEntryPoint
class UserListFragment : Fragment() {

    @Inject
    lateinit var listAdapter: UserListAdapter

    private var binding: FragmentUserListBinding? = null
    private val viewModel by viewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()
    }

    private fun setupObservers() {
        // listener for mobile network connectivity changes
        viewModel.networkState.asLiveData().observe(this) { state ->
            when(state) {
                NetworkStatusState.NetworkStatusConnected -> {
                    binding?.apply {
                        searchView.query?.let {
                            fetchData(it.toString())
                        }
                    }
                }
                else -> {
                    context?.let {
                        displayError(getString(R.string.err_network))
                    }
                }
            }
        }

        viewModel.users.observe(this) {
            lifecycleScope.launch {
                listAdapter.submitData(it)
            }
        }
    }

    private fun setupUI() {

        // search view
        binding?.apply {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                   Log.d("Query : ", query.toString())
                    query?.let {
                        if (it.isNotEmpty()) {
                            recyclerViewUsers.scrollToPosition(0)
                            viewModel.searchUsers(query)
                            searchView.clearFocus()
                        } else {
                            searchView.clearFocus()
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean = false
            })
            searchView.setOnCloseListener {
                searchView.setQuery("", false)
                searchView.clearFocus()
                true
            }

            btnRetry.setOnClickListener{
                listAdapter.retry()
            }

            // recycler view
            recyclerViewUsers.adapter = listAdapter
            recyclerViewUsers.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = listAdapter.withLoadStateFooter(
                    footer = UserLoadStateAdapter { listAdapter.retry() }
                )
                setHasFixedSize(true)
            }
        }

        // call back to get the selected(clicked) item from the list
        listAdapter.onItemClick = { selectedItem, _ ->
            navigateToDetailsScreen(selectedItem)
        }

        // pagination load state UI
        listAdapter.loadStateFlow.asLiveData().observe(viewLifecycleOwner, { loadState ->

            binding?.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerViewUsers.isVisible = loadState.source.refresh is LoadState.NotLoading
                textViewError.isVisible = loadState.source.refresh is LoadState.Error
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error

                // Ui for no results found
                if(loadState.source.refresh is LoadState.NotLoading
                    && loadState.append.endOfPaginationReached
                    && listAdapter.itemCount < 1) {
                    recyclerViewUsers.isVisible = false
                    textViewEmptyResults.isVisible = true
                } else {
                    textViewEmptyResults.isVisible = false
                }
            }
        })
    }

    // fetch the data to be displayed
    private fun fetchData(query: String) {
        if(!viewModel.isDeviceOnline()) {
            displayError(getString(R.string.err_network))
        } else {
            if(query.isNotEmpty())
                viewModel.searchUsers(query)
            else
                binding?.textViewEmptyResults?.isVisible = true
        }
    }

    // navigate to details screen
    private fun navigateToDetailsScreen(selectedItem : RemoteUser) {
        val directions = UserListFragmentDirections.actionListToDetailsView(selectedItem.loginId)
        findNavController().navigate(directions)
    }

    private fun displayError(message: String) {
        binding?.root?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}