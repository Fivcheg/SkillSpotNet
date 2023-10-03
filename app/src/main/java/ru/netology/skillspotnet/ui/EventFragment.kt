package ru.netology.skillspotnet.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.skillspotnet.R
import ru.netology.skillspotnet.adapter.EventAdapter
import ru.netology.skillspotnet.adapter.PagingLoadStateAdapter
import ru.netology.skillspotnet.auth.AppAuth
import ru.netology.skillspotnet.databinding.FragmentFeedBinding
import ru.netology.skillspotnet.dto.Event
import ru.netology.skillspotnet.repository.EventRepository
import ru.netology.skillspotnet.viewmodel.AuthViewModel
import ru.netology.skillspotnet.viewmodel.EventViewModel
import javax.inject.Inject

@AndroidEntryPoint
class EventFragment : Fragment() {
    @Inject
    lateinit var repository: EventRepository

    @Inject
    lateinit var auth: AppAuth
    private val viewModel: EventViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = EventAdapter(object : EventAdapter.OnInteractionListener {
            override fun onEdit(event: Event) {
                viewModel.edit(event)
                findNavController()
                    .navigate(R.id.newEventFragment)
            }

            override fun onLike(event: Event) {
                if (authViewModel.authenticated) {
                    if (!event.likedByMe)
                        viewModel.likeEventById(event.id)
                    else viewModel.dislikeEventById(event.id)
                } else {
                    Toast.makeText(activity, R.string.notAuth, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onRemove(event: Event) {
                viewModel.removeEventById(event.id)
            }

            override fun onShare(event: Event) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, event.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_event))
                startActivity(shareIntent)
            }


            override fun onPlayAudio(event: Event) {
                try {
                    val uri = Uri.parse(event.attachment?.url)
                    val intent = Intent(Intent.ACTION_VIEW)

                    intent.setDataAndType(uri, "audio/*")
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, R.string.error_play, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onPlayVideo(event: Event) {
                try {
                    val uri = Uri.parse(event.attachment?.url)
                    val intent = Intent(Intent.ACTION_VIEW)

                    intent.setDataAndType(uri, "video/*")
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, R.string.error_play, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onOpenImage(event: Event) {
                val bundle = Bundle().apply {
                    putString("url", event.attachment?.url)
                }
                findNavController().navigate(R.id.imageOpenNav, bundle)
            }

            override fun onOpenMap(event: Event) {
                val bundle = Bundle().apply {
                    putString("LAT_KEY", event.coords?.lat)
                    putString("LONG_KEY", event.coords?.long)
                }
                if (event.coords?.lat != null && event.coords?.long != null) {
                    findNavController().navigate(R.id.mapFragment, bundle)
                } else {
                    Toast.makeText(context, R.string.nothing, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter(object : PagingLoadStateAdapter.OnInteractionListener {
                override fun onRetry() {
                    adapter.retry()
                }
            }),
            footer = PagingLoadStateAdapter(object : PagingLoadStateAdapter.OnInteractionListener {
                override fun onRetry() {
                    adapter.retry()
                }
            }),
        )

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest(adapter::submitData)
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                binding.swiperefresh.isRefreshing = it.refresh is LoadState.Loading
            }
        }

        binding.swiperefresh.setOnRefreshListener(adapter::refresh)

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_containerFragmentView_to_newEventFragment)
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = EventFragment()
    }
}
