package ru.netology.skillspotnet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.skillspotnet.R
import ru.netology.skillspotnet.databinding.CardAdBinding
import ru.netology.skillspotnet.databinding.CardPostBinding
import ru.netology.skillspotnet.dto.Ad
import ru.netology.skillspotnet.dto.FeedItem
import ru.netology.skillspotnet.dto.Post
import ru.netology.skillspotnet.enumeration.AttachmentType
import ru.netology.skillspotnet.util.realTimeFormat
import ru.netology.skillspotnet.view.load


class FeedAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(FeedItemDiffCallback()) {
    private val typeAd = 0
    private val typePost = 1

    interface OnInteractionListener {
        fun onLike(post: Post) {}
        fun onEdit(post: Post) {}
        fun onRemove(post: Post) {}
        fun onShare(post: Post) {}
        fun onAdClick(ad: Ad) {}
        fun onOpenImage(post: Post) {}
        fun onPlayAudio(post: Post) {}
        fun onPlayVideo(post: Post) {}
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Ad -> typeAd
            is Post -> typePost
            null -> throw IllegalArgumentException("unknown item type")
            else -> {
                0
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            typeAd -> AdViewHolder(
                CardAdBinding.inflate(layoutInflater, parent, false),
                onInteractionListener
            )

            typePost -> PostViewHolder(
                CardPostBinding.inflate(layoutInflater, parent, false),
                onInteractionListener
            )

            else -> throw IllegalArgumentException("unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when (it) {
                is Post -> (holder as? PostViewHolder)?.bind(it)
                is Ad -> (holder as? AdViewHolder)?.bind(it)
                else -> {
                    0
                }
            }
        }
    }

    class PostViewHolder(
        private val binding: CardPostBinding,
        private val onInteractionListener: OnInteractionListener,
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(post: Post) {
            binding.apply {
                author.text = post.author
                published.text = realTimeFormat(post.published)
                content.text = post.content
                like.isChecked = post.likedByMe
                like.text = post.likeOwnerIds.count().toString()
                imagePost.visibility =
                    if (post.attachment != null && post.attachment.type == AttachmentType.IMAGE) View.VISIBLE else View.GONE

                Glide.with(itemView)
                    .load("${post.authorAvatar}")
                    .placeholder(R.drawable.baseline_catching_pokemon_24)
                    .error(R.drawable.baseline_sign_language_24)
                    .timeout(10_000)
                    .circleCrop()
                    .into(avatar)

                post.attachment?.apply {
                    Glide.with(imagePost)
                        .load(this.url)
                        .placeholder(R.drawable.baseline_catching_pokemon_24)
                        .error(R.drawable.baseline_report_gmailerrorred_24)
                        .timeout(10_000)
                        .into(imagePost)
                }

                menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE

                postAudioGroup.visibility =
                    if (post.attachment != null && post.attachment.type == AttachmentType.AUDIO) View.VISIBLE else View.GONE

                playVideo.visibility =
                    if (post.attachment != null && post.attachment.type == AttachmentType.VIDEO) View.VISIBLE else View.GONE

                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.options_post)
                        menu.setGroupVisible(R.id.owned, post.ownedByMe)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    onInteractionListener.onRemove(post)
                                    true
                                }

                                R.id.edit -> {
                                    onInteractionListener.onEdit(post)
                                    true
                                }

                                else -> false
                            }
                        }
                    }.show()
                }

                like.setOnClickListener {
                    onInteractionListener.onLike(post)
                }

                share.setOnClickListener {
                    onInteractionListener.onShare(post)
                }

                imagePost.setOnClickListener {
                    onInteractionListener.onOpenImage(post)
                }

                playAudio.setOnClickListener {
                    onInteractionListener.onPlayAudio(post)
                }

                playVideo.setOnClickListener {
                    onInteractionListener.onPlayVideo(post)
                }
            }
        }
    }

    class AdViewHolder(
        private val binding: CardAdBinding,
        private val onInteractionListener: OnInteractionListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ad: Ad) {
            binding.apply {
                image.load("https://cs13.pikabu.ru/post_img/2023/05/20/9/1684592976157428245.jpg")
                image.setOnClickListener {
                    onInteractionListener.onAdClick(ad)
                }
            }
        }
    }

    class FeedItemDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
        override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            if (oldItem::class != newItem::class) {
                return false
            }
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            return oldItem == newItem
        }
    }
}

