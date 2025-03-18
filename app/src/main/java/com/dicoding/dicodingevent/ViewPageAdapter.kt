package com.dicoding.dicodingevent


import androidx.fragment.app.Fragment

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dicoding.dicodingevent.ui.favorite.FavoriteFragment
import com.dicoding.dicodingevent.ui.finished.FinishedFragment
import com.dicoding.dicodingevent.ui.home.HomeFragment
import com.dicoding.dicodingevent.ui.setting.SettingFragment
import com.dicoding.dicodingevent.ui.upcoming.UpcomingFragment


class ViewPagerAdapter(activity: MainActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment{
        return when (position) {
            0 -> HomeFragment()
            1 -> UpcomingFragment()
            2 -> FinishedFragment()
            3 -> FavoriteFragment()
            4 -> SettingFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}

