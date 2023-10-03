package ru.netology.skillspotnet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.skillspotnet.R
import ru.netology.skillspotnet.adapter.TabsAdapter
import ru.netology.skillspotnet.databinding.FragmentUserProfileBinding
import ru.netology.skillspotnet.repository.PostRepository


import javax.inject.Inject

@AndroidEntryPoint
class FragmentContainerProfileView : Fragment() {

    @Inject
    lateinit var repository: PostRepository

    private val fragList =
        listOf(WallFragment.newInstance(), JobFragment.newInstance())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        val fragListTitle =
            listOf(getString(R.string.posts), getString(R.string.jobs))

        binding.userName.text = arguments?.getString("name")

        Glide.with(this)
            .load(arguments?.getString("avatar"))
            .placeholder(R.drawable.baseline_catching_pokemon_24)
            .error(R.drawable.baseline_sign_language_24)
            .timeout(10_000)
            .circleCrop()
            .into(binding.userAvatar)


        val id = arguments?.getString("id")?.toLong()


        val adapter = TabsAdapter(this, fragList)
        binding.profileFragment.adapter = adapter
        TabLayoutMediator(binding.profileAppTabs, binding.profileFragment) { tab, pos ->
            tab.text = fragListTitle[pos]
        }.attach()
        return binding.root
    }

}