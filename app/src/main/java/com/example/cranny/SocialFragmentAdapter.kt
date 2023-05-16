package com.example.cranny

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter

class SocialFragmentAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    var fragmentList: ArrayList<Fragment> = ArrayList()

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    fun addFragment(fragment: Fragment, title: String)
    {
        fragmentList.add(fragment)
    }
}