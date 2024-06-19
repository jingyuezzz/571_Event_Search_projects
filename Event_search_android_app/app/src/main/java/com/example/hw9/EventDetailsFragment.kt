package com.example.hw9

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class EventDetailsFragment : Fragment() {

    private lateinit var event: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        event = Event("", "", "", "", "", "", "", "", "", "", "","") // initialize with default value
        arguments?.let {
            event = it.getSerializable("event") as Event
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_event_details, container, false)

        // Populate the view with details of the event
        val artistsTextView = view.findViewById<TextView>(R.id.artists_input)
        artistsTextView.text = event.artist
        artistsTextView.isSelected = true
        val detailVenueTextView = view.findViewById<TextView>(R.id.detailVenue_input)
        detailVenueTextView.text = event.venueName
        val detailDateTextView = view.findViewById<TextView>(R.id.detailDate_input)
        detailDateTextView.text = event.date
        val detailTimeTextView = view.findViewById<TextView>(R.id.detailTime_input)
        detailTimeTextView.text = event.time
        val genresTextView = view.findViewById<TextView>(R.id.genres_input)
        genresTextView.text = event.category
        val priceRangesTextView = view.findViewById<TextView>(R.id.priceRanges_input)
        priceRangesTextView.text = event.priceRange
        val statusTextView = view.findViewById<TextView>(R.id.status_input)
        statusTextView.text = event.status
        val buyTicketUrlTextView = view.findViewById<TextView>(R.id.buyUrl_input)
        buyTicketUrlTextView.text = event.buyUrl
        buyTicketUrlTextView.isSelected = true

        val venue_img = view.findViewById<ImageView>(R.id.venue_image)
        Glide.with(view)
            .load(event.venueImg)
            .into(venue_img)


        return view
    }

    companion object {
        private const val ARG_EVENT = "event"
        fun newInstance(event: Parcelable): EventDetailsFragment {
            val args = Bundle().apply {
                putParcelable(ARG_EVENT, event)
            }
            return EventDetailsFragment().apply {
                arguments = args
            }
        }
    }

}
