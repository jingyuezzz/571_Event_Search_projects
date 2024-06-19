package com.example.hw9

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide


data class Artist(
    val name: String,
    val followers: Int,
    val popularity: Int,
    val link: String,
    val id: String,
    val photo: String,
    val albumPhoto: MutableList<String>
)

class ArtistFragment : Fragment() {
    private lateinit var event: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        event = Event("", "", "", "", "", "", "", "", "", "", "","") // initialize with default value
        arguments?.let {
            event = it.getSerializable("event") as Event
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_artist, container, false)
        if (event.genre != "Music") {
            view.findViewById<LinearLayout>(R.id.text_artist_unavailable).visibility = View.VISIBLE

            return view
        } else {

            val artistRecyclerView = view.findViewById<RecyclerView>(R.id.recycler_artist)
            artistRecyclerView.layoutManager = LinearLayoutManager(requireContext())

            val artistNames = event.artist.split(",") // Get all the artists in the list
            val requestQueue = Volley.newRequestQueue(requireContext())
            val artistList = mutableListOf<Artist>()
            for (artistName in artistNames) {
                val spot_url =
                    "https://hw571-jingyue.wl.r.appspot.com/searchEvent?artist=$artistName"

                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.GET, spot_url, null,
                    { response ->

                        val body = response.optJSONObject("body")

                        val artists = body?.optJSONObject("artists")

                        val art_items = artists?.optJSONArray("items")

                        if (artists != null && artists.length() > 0) {
                            val artistObject = art_items?.optJSONObject(0)
                            val artistID = artistObject?.optString("id")
                            var albumPic = mutableListOf<String>()
                            val album_backlink =
                                "https://hw571-jingyue.wl.r.appspot.com/searchEvent?artistID=" + artistID;
                            val albumRequest =
                                JsonObjectRequest(Request.Method.GET, album_backlink, null,
                                    { response ->

                                        val album_body = response.optJSONObject("body")
                                        val albumArray = album_body.getJSONArray("items")
                                        val albumPhotos = mutableListOf<String>()
                                        for (i in 0 until albumArray.length()) {
                                            val albumObject = albumArray.getJSONObject(i)
                                            val albumPhoto =
                                                albumObject.getJSONArray("images").getJSONObject(0)
                                                    .getString("url")
                                            albumPhotos.add(albumPhoto)
                                        }
                                        albumPic = albumPhotos

                                        val artist = artistObject?.optJSONObject("followers")?.let {
                                            artistObject.optJSONArray("images")?.optJSONObject(0)
                                                ?.let { it1 ->
                                                    Artist(
                                                        artistObject.optString("name"),
                                                        it.optInt("total"),
                                                        artistObject.optInt("popularity"),
                                                        artistObject.optJSONObject("external_urls").optString("spotify"),
                                                        artistObject.optString("id"),
                                                        it1.optString("url"),
                                                        albumPic
                                                    )
                                                }
                                        }
                                        if (artist != null) {
                                            artistList.add(artist)
                                            Log.d(TAG, "Artist list size: ${artistList}")
                                            artistRecyclerView.adapter?.notifyDataSetChanged()
                                        }
                                    },
                                    { error ->
                                        Log.e(TAG, "Error fetching album photos: ${error.message}")
                                    })
                            requestQueue.add(albumRequest)


                        }


                    },
                    { error ->
                        Log.e(TAG, "Error occurred while getting artist info: $error")
                    }
                )

                requestQueue.add(jsonObjectRequest)
            }

            artistRecyclerView.adapter = ArtistAdapter(artistList)

            return view
        }
    }

    companion object {
        private const val ARG_EVENT = "event"
        fun newInstance(event: Parcelable): ArtistFragment {
            val args = Bundle().apply {
                putParcelable(ARG_EVENT, event)
            }
            return ArtistFragment().apply {
                arguments = args
            }
        }
    }
}