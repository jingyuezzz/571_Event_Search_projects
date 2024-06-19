package com.example.hw9

import android.app.appsearch.SearchResult
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import com.bumptech.glide.Glide
import java.io.Serializable


data class Event(
    val eventName: String,
    val venueName: String,
    val category: String,
    val date: String,
    val time: String,
    val artist:String,
    val priceRange:String,
    val status:String,
    val venueImg: String,
    val buyUrl:String,
    val genre:String,
    val image: String

): Serializable
lateinit var searchResultAdapter: SearchResultAdapter

class SearchResultAdapter(private var events: List<Event>) :
    RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        searchResultAdapter = SearchResultAdapter(emptyList())

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_card, parent, false)
        return ViewHolder(view)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        lateinit var searchResultAdapter: SearchResultAdapter
        val isFavoriteArray = Array(events?.size ?: 0) { false }
        Log.d("tag", Arrays.toString(isFavoriteArray))
        val event = events[position]
        holder.eventName.text = event.eventName
        holder.eventName.isSelected = true
        holder.venueName.text = event.venueName
        holder.venueName.isSelected = true
        holder.category.text = event.category
        holder.category.isSelected = true
        holder.date.text = event.date
        holder.time.text = event.time
        holder.favoriteButton.visibility = View.VISIBLE

        var inlist = false
        var isFavorite = isFavoriteArray[position]
        val viewModel = ViewModelProvider(holder.itemView.context as MainActivity).get(EventViewModel::class.java)
        if (viewModel.favoriteEvents.value?.contains(event) == true){
            isFavorite = true
            inlist = true
        }
        else {isFavorite = false}

        if (isFavorite) {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_red)
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_transparent)
        }
        holder.favoriteButton.setOnClickListener {

            //isFavoriteArray[position] = !isFavoriteArray[position]
            val isFavorite = isFavoriteArray[position]
            if (!inlist) {
                holder.favoriteButton.setImageResource(R.drawable.ic_favorite_red)
                Toast.makeText(holder.itemView.context, "${event.eventName} added to favorites", Toast.LENGTH_SHORT).show()
                inlist = true
            }
            else {
                holder.favoriteButton.setImageResource(R.drawable.ic_favorite_transparent)
                inlist = false
                Toast.makeText(holder.itemView.context, "${event.eventName} removed from favorites", Toast.LENGTH_SHORT).show()
            }


            viewModel.toggleFavorite(event)
        }

        if (event.category == "Basketball") {
            Glide.with(holder.itemView.context)
                .load(R.drawable.img)
                .into(holder.categoryImage)

        }
        else {

            Glide.with(holder.itemView.context)
                .load(Uri.parse(event.image))
                .override(2048, 1152)
                .into(holder.categoryImage)
        }


        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, EventDetailsActivity::class.java)
            intent.putExtra("event", event as Serializable)
            holder.itemView.context.startActivity(intent)
        }


    }

    override fun getItemCount() = events.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImage: ImageView = itemView.findViewById(R.id.category_image)
        val eventName: TextView = itemView.findViewById(R.id.event_name)
        val venueName: TextView = itemView.findViewById(R.id.venue_name)
        val category: TextView = itemView.findViewById(R.id.category)
        val date: TextView = itemView.findViewById(R.id.date)
        val time: TextView = itemView.findViewById(R.id.time)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.favorite_button1)


    }
    fun setEvents(events: List<Event>) {
        this.events = events
        notifyDataSetChanged()
    }

}
    class EventViewModel : ViewModel() {
        private val favoriteEventIds = MutableLiveData<List<String>>()

        fun getFavoriteEventIds(): LiveData<List<String>> {
            return favoriteEventIds
        }

        fun updateFavoriteEventIds() {
            favoriteEventIds.value = favoriteEvents.value?.map { "${it.eventName}-${it.date}" }
        }

        private val _favoriteEvents = MutableLiveData<List<Event>>()
        val favoriteEvents: LiveData<List<Event>> get() = _favoriteEvents

        fun toggleFavorite(event: Event) {
            val updatedList = mutableListOf<Event>()
            updatedList.addAll(favoriteEvents.value ?: emptyList())

            if (updatedList.contains(event)) {
                updatedList.remove(event)
            } else {
                updatedList.add(event)
            }

            _favoriteEvents.value = updatedList
            searchResultAdapter.setEvents(updatedList)
        }

    }





