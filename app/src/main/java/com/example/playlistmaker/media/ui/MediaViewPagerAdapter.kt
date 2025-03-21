package com.example.playlistmaker.media.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.media.presentation.view_model.FavoritesFragment
import com.example.playlistmaker.media.presentation.view_model.PlaylistsFragment

class MediaViewPagerAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    private val fragments = listOf(
        FavoritesFragment.newInstance(),
        PlaylistsFragment.newInstance(),
    )
    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment = fragments[position]
}