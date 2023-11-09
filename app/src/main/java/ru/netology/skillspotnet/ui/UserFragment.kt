package ru.netology.skillspotnet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import ru.netology.skillspotnet.R
import ru.netology.skillspotnet.adapter.UserAdapter
import ru.netology.skillspotnet.auth.AppAuth
import ru.netology.skillspotnet.databinding.FragmentUsersBinding
import ru.netology.skillspotnet.dto.User
import ru.netology.skillspotnet.viewmodel.AuthViewModel
import ru.netology.skillspotnet.viewmodel.EventViewModel
import ru.netology.skillspotnet.viewmodel.PostViewModel
import ru.netology.skillspotnet.viewmodel.UserViewModel
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class UserFragment : Fragment() {
    @Inject
    lateinit var auth: AppAuth
    private val userViewModel by activityViewModels<UserViewModel>()
    private val postViewModel by activityViewModels<PostViewModel>()
    private val authViewModel by activityViewModels<AuthViewModel>()
    private val eventViewModel by activityViewModels<EventViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentUsersBinding.inflate(inflater, container, false)
        val userView: Boolean = arguments?.getBoolean("CLICK_VIEW") ?: false
        val checkRole = arguments?.getString("PRESS_ADD")
        val idsCheck =
            when (checkRole) {
                "ADD_MENTION" -> {
                    postViewModel.edited.value?.mentionIds
                }

                "PICK_SPEAKER" -> {
                    eventViewModel.edited.value?.speakerIds
                }

                "PICK_PARTICIPANTS" -> {
                    eventViewModel.edited.value?.participantsIds
                }

                else -> {
                    emptyList()
                }
            }

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
                    if (postViewModel.edited.value?.mentionIds!!.contains(user.id.toInt())) {
                        postViewModel.unSetMentionIds(user.id)
                    } else {
                        postViewModel.setMentionIds(user.id)
                    }
                } else {
                    Toast.makeText(activity, R.string.notAuth, Toast.LENGTH_SHORT)
                        .show()
                }
                postViewModel.edited.value?.mentionIds!!
            }

            override fun onPickSpeaker(user: User) {
                if (authViewModel.authenticated) {
                    if (eventViewModel.edited.value?.speakerIds!!.contains(user.id.toInt())) {
                        eventViewModel.unPickSpeakerIds(user.id)
                    } else {
                        eventViewModel.pickSpeakerIds(user.id)
                    }
                } else {
                    Toast.makeText(activity, R.string.notAuth, Toast.LENGTH_SHORT)
                        .show()
                }
                eventViewModel.edited.value?.speakerIds!!
            }

            override fun onPickParticipants(user: User) {
                if (authViewModel.authenticated) {
                    if (eventViewModel.edited.value?.participantsIds!!.contains(user.id.toInt())) {
                        eventViewModel.unPickParticipantsIds(user.id)
                    } else {
                        eventViewModel.pickParticipantsIds(user.id)
                    }
                } else {
                    Toast.makeText(activity, R.string.notAuth, Toast.LENGTH_SHORT)
                        .show()
                }
                eventViewModel.edited.value?.participantsIds!!
            }

        }, idsCheck, checkRole)

        binding.fabSaveUser.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.listUsers.adapter = adapter

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

        binding.fabSaveUser.visibility =
            if ((auth.authStateFlow.value.id != 0L || auth.authStateFlow.value.token != null) && checkRole != null) {
                VISIBLE
            } else {
                GONE
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