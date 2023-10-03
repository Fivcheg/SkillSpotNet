package ru.netology.skillspotnet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.skillspotnet.R
import ru.netology.skillspotnet.adapter.TabsAdapter
import ru.netology.skillspotnet.databinding.FragmentContainerBinding
import ru.netology.skillspotnet.repository.PostRepository
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
