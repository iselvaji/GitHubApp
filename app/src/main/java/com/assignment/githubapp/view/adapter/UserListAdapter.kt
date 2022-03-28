package com.assignment.githubapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.assignment.githubapp.R
import com.assignment.githubapp.databinding.ListItemUserBinding
import com.assignment.githubapp.model.RemoteUser
import javax.inject.Inject

/**
 * Paging Adapter with for the list of users to display by [UserViewHolder]
 */
class UserListAdapter @Inject constructor() : PagingDataAdapter<RemoteUser,
        UserListAdapter.UserViewHolder>(DiffCallBack) {

    var onItemClick: ((item: RemoteUser, view: View) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding  = ListItemUserBinding.inflate(inflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    object DiffCallBack : DiffUtil.ItemCallback<RemoteUser>() {
        override fun areItemsTheSame(oldItem: RemoteUser, newItem: RemoteUser): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RemoteUser, newItem: RemoteUser): Boolean {
            return oldItem == newItem
        }
    }

    inner class UserViewHolder(private var binding: ListItemUserBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(data: RemoteUser) {
            binding.apply {
                imgUser.load(data.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_loading)
                    error(R.drawable.ic_error)
                }
                title.text = data.loginId
            }
        }

        override fun onClick(view: View?) {
            if (view != null) {
                getItem(absoluteAdapterPosition)?.let { onItemClick?.invoke(it, view) }
            }
        }
    }
}