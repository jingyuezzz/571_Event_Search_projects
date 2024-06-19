package com.example.hw9

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.Serializable


class FavoritesFragment : Fragment() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var searchResultAdapter: SearchResultAdapter
    private var isFavoriteArray = emptyArray<Boolean>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)
        eventViewModel = ViewModelProvider(requireActivity()).get(EventViewModel::class.java)


        // Create a new instance of SearchResultAdapter with an empty list
        searchResultAdapter = SearchResultAdapter(emptyList())

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_fav)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = searchResultAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventViewModel = ViewModelProvider(requireActivity()).get(EventViewModel::class.java)
        eventViewModel.favoriteEvents.observe(viewLifecycleOwner) { favoriteEvents ->
            searchResultAdapter.setEvents(favoriteEvents)
            searchResultAdapter.notifyDataSetChanged()
        }
    }
}






