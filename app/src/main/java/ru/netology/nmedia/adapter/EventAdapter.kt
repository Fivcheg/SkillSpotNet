package ru.netology.nmedia.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardEventBinding
import ru.netology.nmedia.dto.AdEvent
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.EventItem
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.util.formatToDate
import ru.netology.nmedia.util.realTimeFormat
import ru.netology.nmedia.view.load


class EventAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<EventItem, RecyclerView.ViewHolder>(EventItemDiffCallback()) {
    private val typeAd = 0
    private val typeEvent = 1

    interface OnInteractionListener {
        fun onLike(event: Event) {}
        fun onEdit(event: Event) {}
        fun onRemove(event: Event) {}
        fun onShare(event: Event) {}
        fun onAdClick(ad: AdEvent) {}
        fun onOpenImage(event: Event) {}
        fun onPlayAudio(event: Event) {}
        fun onPlayVideo(event: Event) {}
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is AdEvent -> typeAd
            is Event -> typeEvent
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

            typeEvent -> EventViewHolder(
                CardEventBinding.inflate(layoutInflater, parent, false),
                onInteractionListener
            )

            else -> throw IllegalArgumentException("unknown view type: $viewType")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when (it) {
                is Event -> (holder as? EventViewHolder)?.bind(it)
                is AdEvent -> (holder as? AdViewHolder)?.bind(it)
                else -> {
                    0
                }
            }
        }
    }

    class EventViewHolder(
        private val binding: CardEventBinding,
        private val onInteractionListener: OnInteractionListener,
    ) : RecyclerView.ViewHolder(binding.root) {


        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(event: Event) {
            binding.apply {

                author.text = event.author
                published.text = realTimeFormat(event.published)
                content.text = event.content
                like.isChecked = event.likedByMe
                dateTimeEventValue.text = formatToDate(event.datetime)
                buttonSpeakersEvent.text = event.speakerIds?.count().toString()

                imageEvent.visibility =
                    if (event.attachment != null && event.attachment.type == AttachmentType.IMAGE) View.VISIBLE else View.GONE

                Glide.with(itemView)
                    .load("${event.authorAvatar}")
                    .placeholder(R.drawable.baseline_catching_pokemon_24)
                    .error(R.drawable.baseline_sign_language_24)
                    .timeout(10_000)
                    .circleCrop()
                    .into(avatar)

                event.attachment?.apply {
                    Glide.with(imageEvent)
                        .load(this.url)
                        .placeholder(R.drawable.baseline_catching_pokemon_24)
                        .error(R.drawable.baseline_report_gmailerrorred_24)
                        .timeout(10_000)
                        .into(imageEvent)
                }

                menu.visibility = if (event.ownedByMe) View.VISIBLE else View.INVISIBLE

                eventAudioGroup.visibility =
                    if (event.attachment != null && event.attachment.type == AttachmentType.AUDIO) View.VISIBLE else View.GONE

                playVideoEvent.visibility =
                    if (event.attachment != null && event.attachment.type == AttachmentType.VIDEO) View.VISIBLE else View.GONE

                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.options_post)
                        menu.setGroupVisible(R.id.owned, event.ownedByMe)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    onInteractionListener.onRemove(event)
                                    true
                                }

                                R.id.edit -> {
                                    onInteractionListener.onEdit(event)
                                    true
                                }

                                else -> false
                            }
                        }
                    }.show()
                }

                like.setOnClickListener {
                    onInteractionListener.onLike(event)
                }

                share.setOnClickListener {
                    onInteractionListener.onShare(event)
                }

                imageEvent.setOnClickListener {
                    onInteractionListener.onOpenImage(event)
                }

                playAudioEvent.setOnClickListener {
                    onInteractionListener.onPlayAudio(event)
                }

                playVideoEvent.setOnClickListener {
                    onInteractionListener.onPlayVideo(event)
                }
            }
        }
    }

    class AdViewHolder(
        private val binding: CardAdBinding,
        private val onInteractionListener: OnInteractionListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ad: AdEvent) {
            binding.apply {
                image.load("https://cs13.pikabu.ru/post_img/2023/05/20/9/1684592976157428245.jpg")
                image.setOnClickListener {
                    onInteractionListener.onAdClick(ad)
                }
            }
        }
    }

    class EventItemDiffCallback : DiffUtil.ItemCallback<EventItem>() {
        override fun areItemsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
            if (oldItem::class != newItem::class) {
                return false
            }
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
            return oldItem == newItem
        }
    }
}

