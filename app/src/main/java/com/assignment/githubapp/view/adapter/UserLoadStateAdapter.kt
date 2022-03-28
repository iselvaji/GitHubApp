package com.assignment.githubapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.assignment.githubapp.databinding.PaginationLoadingStateBinding

/**
 * Adapter to display the loading state of pagination.
 * Load states are Loading, Error, Not Loading
 */
class UserLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<UserLoadStateAdapter.ArtLoadStateViewHolder>() {

    inner class ArtLoadStateViewHolder(
        private val binding: PaginationLoadingStateBinding,
        private val retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            // Log.d("load state : ", loadState.toString())

            binding.progressbar.visible(loadState is LoadState.Loading)
            binding.textRefresh.visible(loadState is LoadState.Error)
            binding.textError.visible(loadState is LoadState.Error)
            binding.textRefresh.setOnClickListener {
                retry()
            }
        }
    }

    override fun onBindViewHolder(holder: ArtLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) = ArtLoadStateViewHolder(
        PaginationLoadingStateBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        retry
    )

    fun View.visible(isVisible: Boolean) {
        visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}