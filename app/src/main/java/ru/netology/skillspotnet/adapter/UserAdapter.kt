package ru.netology.skillspotnet.adapter

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.skillspotnet.R
import ru.netology.skillspotnet.databinding.CardUsersBinding
import ru.netology.skillspotnet.dto.Post
import ru.netology.skillspotnet.dto.User

class UserAdapter(
    private val onUserInteractionListener: OnUserInteractionListener,
    private val showAddUsers: Boolean,
) : ListAdapter<User, UserViewHolder>(UserDiffCallback()) {

    interface OnUserInteractionListener {
        fun onOpenUser(user: User)
        fun onAddMentions(user: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = CardUsersBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding, onUserInteractionListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}

class UserViewHolder(
    private val binding: CardUsersBinding,
    private val onUserInteractionListener: UserAdapter.OnUserInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {
        binding.apply {
            userName.text = user.name
            userLogin.text = user.login
            Glide.with(userAvatar)
                .load("${user.avatar}")
                .placeholder(R.drawable.baseline_terrain_24)
                .error(R.drawable.baseline_insert_emoticon_24)
                .timeout(10_000)
                .circleCrop()
                .into(userAvatar)

            userView.setOnClickListener {
                onUserInteractionListener.onOpenUser(user)
            }

            addUser.visibility = VISIBLE

            addUser.setOnClickListener {
                onUserInteractionListener.onAddMentions(user)
            }
        }
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}
