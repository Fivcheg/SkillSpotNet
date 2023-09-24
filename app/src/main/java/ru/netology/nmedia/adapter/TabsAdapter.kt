package ru.netology.nmedia.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabsAdapter(
    fragmentActivity: Fragment,
    private val listTabsFragment: List<Fragment>
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return listTabsFragment.size
    }

    override fun createFragment(position: Int): Fragment {
        return listTabsFragment[position]
    }
}