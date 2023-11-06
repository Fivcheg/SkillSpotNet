package ru.netology.skillspotnet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import ru.netology.skillspotnet.R
import ru.netology.skillspotnet.adapter.UserAdapter
import ru.netology.skillspotnet.auth.AppAuth
import ru.netology.skillspotnet.databinding.CardUsersBinding
import ru.netology.skillspotnet.databinding.FragmentUsersBinding
import ru.netology.skillspotnet.dto.Post
import ru.netology.skillspotnet.dto.User
import ru.netology.skillspotnet.viewmodel.AuthViewModel
import ru.netology.skillspotnet.viewmodel.EventViewModel
import ru.netology.skillspotnet.viewmodel.PostViewModel
import ru.netology.skillspotnet.viewmodel.UserViewModel
import javax.inject.Inject
import kotlin.io.path.fileVisitor

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class UserFragment : Fragment() {
    @Inject
    lateinit var auth: AppAuth
    private val userViewModel: UserViewModel by activityViewModels()
    private val postViewModel by activityViewModels<PostViewModel>()
    private val authViewModel by activityViewModels<AuthViewModel>()
    private val eventViewModel by activityViewModels<EventViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentUsersBinding.inflate(inflater, container, false)
        val showAddUsers: Boolean = false

        val adapter = UserAdapter(object : UserAdapter.OnUserInteractionListener {
            override fun onOpenUser(user: User) {
                userViewModel.getUserById(user.id)
                val bundle = Bundle().apply {
                    putLong("id", user.id)
                    putString("avatar", user.avatar)
                    putString("name", user.name)
                }
                findNavController().navigate(R.id.userProfileFragment, bundle)
            }

            override fun onAddMentions(user: User) {
                    if (authViewModel.authenticated) {
                   // if (postViewModel.edited.value?.mentionIds != null) {

                        if (postViewModel.edited.value!!.mentionIds == null || postViewModel.edited.value?.mentionIds!!.contains(user.id.toInt())) {
                            postViewModel.setMentionIds(user.id)
                        } else {
                            postViewModel.unSetMentionIds(user.id)
                        }
//                    }
//                    else {
//                        postViewModel.setMentionIds(user.id)
//                    }

                } else {
                    Toast.makeText(activity, R.string.notAuth, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }, showAddUsers)


        binding.fabSaveUser.setOnClickListener {
//            val bundle = Bundle().apply {
//                putString("mentionList", postViewModel.edited.value?.mentionIds.toString())
//            }
            findNavController().navigateUp()
        }

        binding.listUsers.adapter = adapter
        val userView: Boolean = arguments?.getBoolean("CLICK_VIEW_MENTION") ?: false
        val filteredUsers = if (userView) {
            combine(
                userViewModel.data.asFlow(),
                userViewModel.userIds.asFlow().map { it.toSet() }) { users, userIds ->
                users.filter { it.id.toInt() in userIds }
            }
                .asLiveData()
        } else {
            userViewModel.data
        }

        val navFromNewPost: Boolean = arguments?.getString("ADD_MENTION") == "ADD_MENTION"

        if ((auth.authStateFlow.value.id != 0L || auth.authStateFlow.value.token != null) && navFromNewPost) {
            binding.fabSaveUser.visibility = View.VISIBLE

        } else {
            binding.fabSaveUser.visibility = View.GONE
        }

        filteredUsers.observe(viewLifecycleOwner)
        {
            adapter.submitList(it)
        }

        userViewModel.dataState.observe(viewLifecycleOwner)
        {
            when {
                it.error -> {
                    Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserFragment()
    }
}