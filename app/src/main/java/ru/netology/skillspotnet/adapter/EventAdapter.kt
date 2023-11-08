package ru.netology.skillspotnet.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.skillspotnet.R
import ru.netology.skillspotnet.databinding.CardAdBinding
import ru.netology.skillspotnet.databinding.CardEventBinding
import ru.netology.skillspotnet.dto.AdEvent
import ru.netology.skillspotnet.dto.Event
import ru.netology.skillspotnet.dto.EventItem
import ru.netology.skillspotnet.enumeration.AttachmentType
import ru.netology.skillspotnet.util.formatToDate
import ru.netology.skillspotnet.util.realTimeFormat
import ru.netology.skillspotnet.view.load


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
        fun onSpeakerView(event: Event) {}
        fun onPlayAudio(event: Event) {}
        fun onPlayVideo(event: Event) {}
        fun onOpenMap(event: Event) {}
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is AdEvent -> typeAd
            is Event -> typeEvent
            null -> throw IllegalArgumentException("unknown item type")
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
                like.text = event.likeOwnerIds.count().toString()
                dateTimeEventValue.text = formatToDate(event.datetime)
                buttonSpeakersEvent.text = event.speakerIds.count().toString()
                coordinatesEventValue.text =
                    if (event.coords?.lat != null) {
                        "${event.coords.lat.dropLast(13)}, ${event.coords.long.dropLast(10)}"
                    } else {
                        " null"
                    }
                typeEventValue.text = event.type.toString()
                imageEvent.visibility =
                    if (event.attachment != null && event.attachment.type == AttachmentType.IMAGE) VISIBLE else GONE

                Glide.with(itemView)
                    .load("${event.authorAvatar}")
                    .placeholder(R.drawable.baseline_terrain_24)
                    .error(R.drawable.baseline_insert_emoticon_24)
                    .timeout(10_000)
                    .circleCrop()
                    .into(avatar)

                event.attachment?.apply {
                    Glide.with(imageEvent)
                        .load(this.url)
                        .placeholder(R.drawable.baseline_terrain_24)
                        .error(R.drawable.baseline_report_gmailerrorred_24)
                        .timeout(10_000)
                        .into(imageEvent)
                }

                menu.visibility = if (event.ownedByMe) VISIBLE else INVISIBLE

                eventAudioGroup.visibility =
                    if (event.attachment != null && event.attachment.type == AttachmentType.AUDIO) VISIBLE else GONE

                playVideoEvent.visibility =
                    if (event.attachment != null && event.attachment.type == AttachmentType.VIDEO) VISIBLE else GONE

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


                buttonSpeakersEvent.setOnClickListener {
                    onInteractionListener.onSpeakerView(event)
                }

                playVideoEvent.setOnClickListener {
                    onInteractionListener.onPlayVideo(event)
                }

                buttonLocationEvent.setOnClickListener {
                    onInteractionListener.onOpenMap(event)
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

