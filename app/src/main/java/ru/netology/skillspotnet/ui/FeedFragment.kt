package ru.netology.skillspotnet.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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
import ru.netology.skillspotnet.adapter.FeedAdapter
import ru.netology.skillspotnet.adapter.PagingLoadStateAdapter
import ru.netology.skillspotnet.auth.AppAuth
import ru.netology.skillspotnet.databinding.FragmentFeedBinding
import ru.netology.skillspotnet.dto.Post
import ru.netology.skillspotnet.repository.PostRepository
import ru.netology.skillspotnet.viewmodel.AuthViewModel
import ru.netology.skillspotnet.viewmodel.PostViewModel
import ru.netology.skillspotnet.viewmodel.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() {
    @Inject
    lateinit var repository: PostRepository

    @Inject
    lateinit var auth: AppAuth
    private val viewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = FeedAdapter(object : FeedAdapter.OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
                val bundle = Bundle().apply {
                    putString("content", post.content)
                }
                findNavController()
                    .navigate(R.id.newPostFragment, bundle)
            }

            override fun onLike(post: Post) {
                if (authViewModel.authenticated) {
                    if (!post.likedByMe)
                        viewModel.likeById(post.id)
                    else viewModel.dislikeById(post.id)
                } else {
                    Toast.makeText(activity, R.string.notAuth, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onPlayAudio(post: Post) {
                try {
                    val uri = Uri.parse(post.attachment?.url)
                    val intent = Intent(Intent.ACTION_VIEW)

                    intent.setDataAndType(uri, "audio/*")
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, R.string.error_play, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onPlayVideo(post: Post) {
                try {
                    val uri = Uri.parse(post.attachment?.url)
                    val intent = Intent(Intent.ACTION_VIEW)

                    intent.setDataAndType(uri, "video/*")
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, R.string.error_play, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onOpenImage(post: Post) {
                val bundle = Bundle().apply {
                    putString("url", post.attachment?.url)
                }
                findNavController().navigate(R.id.imageOpenNav, bundle)
            }

            override fun onViewMentions(post: Post) {
                if (post.mentionIds.isNotEmpty()) {
                    userViewModel.getUsersIds(post.mentionIds)
                    val bundle = Bundle()
                    bundle.putBoolean("CLICK_VIEW", true)
                    findNavController().navigate(R.id.userFragment, bundle)
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

        binding.fab.visibility =
            if (auth.authStateFlow.value.id == 0L || auth.authStateFlow.value.token == null) {
                GONE
            } else {
                VISIBLE
            }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_containerFragmentView_to_newPostFragment)
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = FeedFragment()
    }
}
