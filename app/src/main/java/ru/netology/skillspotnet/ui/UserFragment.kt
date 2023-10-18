package ru.netology.skillspotnet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import ru.netology.skillspotnet.R
import ru.netology.skillspotnet.adapter.UserAdapter
import ru.netology.skillspotnet.databinding.FragmentUsersBinding
import ru.netology.skillspotnet.dto.Post
import ru.netology.skillspotnet.dto.User
import ru.netology.skillspotnet.viewmodel.AuthViewModel
import ru.netology.skillspotnet.viewmodel.EventViewModel
import ru.netology.skillspotnet.viewmodel.PostViewModel
import ru.netology.skillspotnet.viewmodel.UserViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class UserFragment : Fragment() {

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

    //    val userView = arguments?.getString("MENTION_IDS")?.drop(1)?.dropLast(1)?.split(", ")?.toList()!!.map{it.toInt()}

        val userView = arguments?.getString("MENTION_IDS")

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

            override fun onViewMentions(user: User) {

            }

            override fun onAddMentions(user: User) {
                postViewModel.setMentionIds(user.id)
            }
        })

        binding.fabSaveUser.setOnClickListener {
        //    postViewModel.save()
            findNavController().navigateUp()
        }

        binding.listUsers.adapter = adapter

        userViewModel.data.observe(viewLifecycleOwner)
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