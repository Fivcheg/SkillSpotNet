package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.TabsAdapter
import ru.netology.nmedia.databinding.FragmentContainerBinding
import ru.netology.nmedia.repository.PostRepository
import javax.inject.Inject

@AndroidEntryPoint
class FragmentContainerView : Fragment() {
    @Inject
    lateinit var repository: PostRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    private val fragList =
        listOf(FeedFragment.newInstance(), EventFragment.newInstance(), UserFragment.newInstance())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentContainerBinding.inflate(inflater, container, false)
        val fragListTitle =
            listOf(getString(R.string.posts), getString(R.string.events), getString(R.string.users))


        val adapter = TabsAdapter(this, fragList)
        binding.hostFragment.adapter = adapter
        TabLayoutMediator(binding.mainAppTabs, binding.hostFragment) { tab, pos ->
            tab.text = fragListTitle[pos]
        }.attach()

        return binding.root
    }

}
