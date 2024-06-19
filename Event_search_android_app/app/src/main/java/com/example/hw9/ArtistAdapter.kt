package com.example.hw9

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextClock
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.lang.Math.log
import java.util.*

class ArtistAdapter(private val artists: List<Artist>) : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.artist_card, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(artists[position])
    }

    override fun getItemCount(): Int = artists.size

    class ArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photoView: ImageView = itemView.findViewById(R.id.artist_image)
        private val artistNameView: TextView = itemView.findViewById(R.id.artist_name)
        private val followersCountView: TextView = itemView.findViewById(R.id.folllower)
        private val popularityCountView: TextView = itemView.findViewById(R.id.progress_num)
        private val popularityProgressView: ProgressBar =
            itemView.findViewById(R.id.popularity_progress)
        private val albumsLayout: LinearLayout = itemView.findViewById(R.id.albums_layout)
        private val cover_1: ImageView = itemView.findViewById(R.id.album_cover_1)
        private val cover_2: ImageView = itemView.findViewById(R.id.album_cover_2)
        private val cover_3: ImageView = itemView.findViewById(R.id.album_cover_3)
        private val artistLink :TextView = itemView.findViewById(R.id.spotify_link)




        fun bind(artist: Artist) {
            Glide.with(itemView)
                .load(artist.photo)
                .into(photoView)
            artistNameView.text = artist.name
            followersCountView.text = formatFollowersCount(artist.followers)
            popularityCountView.text = artist.popularity.toString()
            popularityProgressView.progress = artist.popularity
            artistLink.setOnClickListener {
                val webpage = Uri.parse(artist.link)
                Log.d("tag", artist.link)
                val intent = Intent(Intent.ACTION_VIEW,webpage)
                itemView.context.startActivity(intent)
            }


            Glide.with(itemView)
                .load(artist.albumPhoto[0])
                .into(cover_1)
            Glide.with(itemView)
                .load(artist.albumPhoto[1])
                .into(cover_2)

            Glide.with(itemView)
                .load(artist.albumPhoto[2])
                .into(cover_3)


        }
        internal fun formatFollowersCount(followersCount: Int): String {
            return when {
                followersCount >= 1000000 -> "${(followersCount / 1000000)}M"
                followersCount >= 1000 -> "${(followersCount / 1000)}K"
                else -> "$followersCount"
            }
        }
    }






}



