package ru.netology.skillspotnet.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.skillspotnet.R
import ru.netology.skillspotnet.databinding.CardJobsBinding
import ru.netology.skillspotnet.dto.Job
import ru.netology.skillspotnet.util.formatToDate

class JobAdapter(
    private val onJobInteractionListener: OnJobInteractionListener,
) : ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {

    interface OnJobInteractionListener {
        fun onRemoveJob(job: Job)
        fun onEditJob(job: Job)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = CardJobsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return JobViewHolder(parent.context, binding, onJobInteractionListener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}

class JobViewHolder(
    private val context: Context,
    private val binding: CardJobsBinding,
    private val onJobInteractionListener: JobAdapter.OnJobInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(job: Job) {
        binding.apply {
            jobCompanyName.text = job.name
            jobPositionName.text = job.position
            jobStartName.text = formatToDate(job.start)
            jobFinishName.text = if (job.finish != null) formatToDate(job.finish) else null
            jobLinkName.text = job.link
            menuCardJob.visibility = if (job.ownedByMe) VISIBLE else INVISIBLE
            menuCardJob.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.owned, job.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onJobInteractionListener.onRemoveJob(job)
                                true
                            }

                            R.id.edit -> {
                                onJobInteractionListener.onEditJob(job)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}