package com.example.hw9

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonParseException
import org.json.JSONArray
import org.json.JSONException
import com.google.gson.JsonParser
import java.text.SimpleDateFormat
import java.util.*


class SearchResultFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_result, container, false)

        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar1)
        progressBar.visibility = View.VISIBLE
        val responseString = arguments?.getString(RESPONSE_STRING_KEY)
        val events = responseString?.let { parseResponse(it) }

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        // Set the adapter to the RecyclerView

        recyclerView.adapter = SearchResultAdapter(events ?: emptyList())

        progressBar.visibility = View.GONE

        val backButton = view.findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        return view
    }
    private fun parseResponse(response: String): List<Event> {
        val events = mutableListOf<Event>()
        try {
            val responseJson = JsonParser.parseString(response).asJsonObject
            val embedded = responseJson.getAsJsonObject("_embedded")
            val eventsArray = embedded.getAsJsonArray("events")
            Log.d(TAG, "Response1: $responseJson")

            for (i in 0 until eventsArray.size()) {
                val eventJson = eventsArray[i].asJsonObject
                val name = eventJson.get("name").asString
                val dates = eventJson.getAsJsonObject("dates")
                val start = dates.getAsJsonObject("start")
                var localTime = ""
                var localDate = ""
                if (start != null){
                localDate = start.get("localDate").asString
                localTime = start.get("localTime").asString }
                //change time
                val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                val time = outputFormat.format(inputFormat.parse(localTime))
                val images = eventJson.getAsJsonArray("images")
                val image = if (images.size() > 0) images[0].asJsonObject.get("url").asString else null
                val venue = eventJson.getAsJsonObject("_embedded").getAsJsonArray("venues").get(0).asJsonObject
                val venueName = venue.get("name").asString




                val cate = eventJson
                    .getAsJsonArray("classifications")
                    .get(0)
                    .asJsonObject
                val genreObject = cate.getAsJsonObject("genre")
                var category = ""
                if (genreObject != null) {
                    category = genreObject.get("name").asString
                }


                val embedded = eventJson.getAsJsonObject("_embedded")
                val attractions = embedded?.getAsJsonArray("attractions")
                var artist = ""
                if (attractions != null) {
                    val artists = mutableListOf<String>()
                    for (j in 0 until attractions.size()) {
                        val attractionJson = attractions[j].asJsonObject
                        val artistName = attractionJson.get("name").asString
                        artists.add(artistName)
                    }
                    artist = artists.joinToString(separator = ", ")

                } else { artist = "" }

                val segmentName = attractions?.get(0)?.asJsonObject?.get("classifications")?.asJsonArray?.get(0)?.asJsonObject?.get("segment")?.asJsonObject?.get("name")?.asString




                // Get the price ranges from the JSON array
                val priceRanges = eventJson.getAsJsonArray("priceRanges")
                val priceRangeStrings = mutableListOf<String>()
                priceRanges?.let {
                for (j in 0 until priceRanges.size()) {
                    val priceRangeJson = priceRanges[j].asJsonObject
                    val min = priceRangeJson.get("min").asDouble
                    val max = priceRangeJson.get("max").asDouble
                    val currency = priceRangeJson.get("currency").asString
                    val priceRangeString = "$currency$min-$max"
                    priceRangeStrings.add(priceRangeString)
                }}
                val priceRange = priceRangeStrings.joinToString()

                // Get the ticket status URL from the JSON array
                val status = eventJson.getAsJsonObject("dates").get("status").asJsonObject.get("code").asString
                var venueImg = ""
                // Get the venue image URL from the JSON array
                val seatmap = eventJson.getAsJsonObject("seatmap")
                if (seatmap != null && seatmap.has("staticUrl")) {
                    venueImg = seatmap.get("staticUrl").asString
                }

                val buyUrl = eventJson.get("url").asString

                val event = image?.let {
                    if (segmentName != null) {
                        Event(
                            name, venueName, category, localDate, time, artist, priceRange, status,
                            venueImg, buyUrl, segmentName, it
                        )
                    } else {
                        null
                    }
                }
                if (event != null) {
                    events.add(event)
                }

            }
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }
        return events
    }


    companion object {
        private const val RESPONSE_STRING_KEY = "response_string_key"

        fun newInstance(responseString: String): SearchResultFragment {
            val args = Bundle()
            args.putString(RESPONSE_STRING_KEY, responseString)
            val fragment = SearchResultFragment()
            fragment.arguments = args
            return fragment
        }
    }



}



