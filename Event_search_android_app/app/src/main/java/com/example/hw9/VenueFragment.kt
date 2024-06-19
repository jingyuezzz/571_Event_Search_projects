package com.example.hw9

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

data class VenueInfo(
    var Name: String,
    var Address: String,
    var PhoneNumber: String,
    var OpenHours: String,
    var GeneralRule: String,
    var ChildRule: String
)

class VenueFragment : Fragment() {

    private lateinit var event: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        event = Event("", "", "", "", "", "", "", "", "", "", "","") // initialize with default value
        arguments?.let {
            event = it.getSerializable("event") as Event
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_venue, container, false)
        val venuekw = event.venueName
        val requestQueue = Volley.newRequestQueue(requireContext())
        val progressBar = view.findViewById<ProgressBar>(R.id.venueProgressBar)
        progressBar.visibility = View.VISIBLE
        view.findViewById<LinearLayout>(R.id.page1).visibility = View.GONE
        view.findViewById<LinearLayout>(R.id.page2).visibility = View.GONE

        val spot_url = "https://hw571-jingyue.wl.r.appspot.com/searchEvent?venue_kw=$venuekw"
        val venueInfo = VenueInfo("", "", "", "", "", "")
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, spot_url, null,
            { response ->
                val data = response.getJSONObject("_embedded")
                if (data.getJSONArray("venues").length() == 0) {
                    Log.d("TAG", "no venue: " + venuekw)

                } else {
                    val venue = data.getJSONArray("venues")
                    if (venue != null) {
                        val venueObject = venue.getJSONObject(0)
                        val address = venueObject.getJSONObject("address")
                        val city = venueObject.getJSONObject("city").getString("name")
                        val state = venueObject.getJSONObject("state").getString("name")
                        val citynstate = "$city, $state"
                        val lat = venueObject.getJSONObject("location").getString("latitude").toDouble()
                        val lon = venueObject.getJSONObject("location").getString("longitude").toDouble()
                        Log.e("TAG", lat.toString())
                        venueInfo.Name = venueObject.getString("name")
                        venueInfo.Address = address.getString("line1")
                        val boxOfficeInfoObject = venueObject.optJSONObject("boxOfficeInfo")
                        if (boxOfficeInfoObject != null) {
                            venueInfo.PhoneNumber = boxOfficeInfoObject.optString("phoneNumberDetail")
                            venueInfo.OpenHours = boxOfficeInfoObject.getString("openHoursDetail")
                        }
                        else{
                            venueInfo.PhoneNumber = "N/A"
                            venueInfo.OpenHours = "N/A"
                        }
                        val generalInfoObject = venueObject.optJSONObject("generalInfo")
                        if (generalInfoObject != null) {
                            venueInfo.GeneralRule = generalInfoObject.getString("generalRule")
                            venueInfo.ChildRule = generalInfoObject.getString("childRule")
                        }
                        else{
                            venueInfo.GeneralRule = "N/A"
                            venueInfo.ChildRule = "N/A"
                        }

                        val line1 = view.findViewById<TextView>(R.id.venueName_input)
                        line1.text = venueInfo.Name
                        line1.isSelected = true

                        val line2 = view.findViewById<TextView>(R.id.venueAddress_input)
                        line2.text = venueInfo.Address
                        line2.isSelected = true

                        val line3 =view.findViewById<TextView>(R.id.venueContact_input)
                        line3.text = venueInfo.PhoneNumber
                        line3.isSelected = true

                        val line4 = view.findViewById<TextView>(R.id.venueCity_input)
                        line4.text = citynstate
                        line4.isSelected = true

                        view.findViewById<TextView>(R.id.open_hour).text = venueInfo.OpenHours
                        view.findViewById<TextView>(R.id.gen_rule).text = venueInfo.GeneralRule
                        view.findViewById<TextView>(R.id.child_rule).text = venueInfo.ChildRule

                        // Initialize the GoogleMap object
                        if (lat != null && lon != null) {
                            val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
                            mapFragment?.getMapAsync { googleMap ->
                                val venueLocation = LatLng(lat, lon)
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(venueLocation, 14f))
                                googleMap.addMarker(MarkerOptions().position(venueLocation).title("Venue"))
                            }
                        } else {
                            Log.e(TAG, "Latitude or longitude is null")
                        }



                    }
                }
                view.findViewById<LinearLayout>(R.id.page1).visibility = View.VISIBLE
                view.findViewById<LinearLayout>(R.id.page2).visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            },
            { error ->
                Log.e("TAG", error.toString())
                view.findViewById<LinearLayout>(R.id.page1).visibility = View.VISIBLE
                view.findViewById<LinearLayout>(R.id.page2).visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        )
        requestQueue.add(jsonObjectRequest)

        return view
    }


    companion object {
        private const val ARG_EVENT = "event"
        fun newInstance(event: Parcelable): VenueFragment {
            val args = Bundle().apply {
                putParcelable(ARG_EVENT, event)
            }
            return VenueFragment().apply {
                arguments = args
            }
        }
    }
}