package com.example.hw9

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class EventDetailsPagerAdapter(fragmentActivity: FragmentActivity, private val event: Event) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                val fragment = EventDetailsFragment()
                val args = Bundle().apply {
                    putSerializable("event", event)
                }
                fragment.arguments = args
                fragment
            }
            1 -> {
                val fragment = ArtistFragment()
                val args = Bundle().apply {
                    putSerializable("event", event)
                }
                fragment.arguments = args
                fragment
            }
            2 -> {
                val fragment =  VenueFragment()
                val args = Bundle().apply {
                    putSerializable("event", event)
                }
                fragment.arguments = args
                fragment
            }
            else -> throw IllegalArgumentException("Invalid tab position: $position")
        }
    }
}
