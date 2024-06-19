package com.example.hw9

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import androidx.appcompat.widget.SwitchCompat


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        //autocomplete
        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.autocomplete_textview)
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1
        autoCompleteTextView.addTextChangedListener(createTextWatcher(adapter))
        //category
        val spinner = view.findViewById<Spinner>(R.id.spinner2)
        val categories = resources.getStringArray(R.array.categories_array)
        val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter1
        spinner.setSelection(0)

        val spinner_unit = view.findViewById<Spinner>(R.id.spinner1)
        val units = resources.getStringArray(R.array.units_array)
        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, units)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_unit.adapter = adapter2
        spinner_unit.setSelection(0)
        var autodetect = view.findViewById<SwitchCompat>(R.id.switch1)
        val locEditText = view.findViewById<EditText>(R.id.location_edit)

        val disEditText = view.findViewById<EditText>(R.id.distance_edittext)
        val keywords = autoCompleteTextView.text.toString()
        val location = locEditText.text.toString()
        val distance = disEditText.text.toString()
        val cate = spinner.selectedItem.toString()

        autodetect.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                locEditText.visibility = View.GONE
            } else {
                locEditText.visibility = View.VISIBLE
            }
        }
        val clearButton = view.findViewById<Button>(R.id.button2)
        clearButton.setOnClickListener {
            disEditText.setText("10")
            autoCompleteTextView.setText("")
            locEditText.setText("")
            spinner.setSelection(0)
            autodetect.isChecked = false
            locEditText.visibility = View.VISIBLE
        }

        val searchButton = view.findViewById<Button>(R.id.button)
        val baseUrl = "https://hw571-jingyue.wl.r.appspot.com/searchEvent"
        // Set up click listener for the search button

        searchButton.setOnClickListener {

            var autodetect = view.findViewById<SwitchCompat>(R.id.switch1)
            val locEditText = view.findViewById<EditText>(R.id.location_edit)

            val disEditText = view.findViewById<EditText>(R.id.distance_edittext)
            val keywords = autoCompleteTextView.text.toString()
            val location = locEditText.text.toString()
            val distance = disEditText.text.toString()
            val cate = spinner.selectedItem.toString()

            if (keywords.isBlank()) {
                Toast.makeText(context, "Enter a keyword", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (distance.isBlank()) {
                Toast.makeText(context, "Enter a distance", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Check if a location is entered or autodetect is checked
            if (location.isBlank() && !autodetect.isChecked) {
                Toast.makeText(context, "Enter a location or check autodetect", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //change cate to segid
            val segid = if (cate == "Music") {
                "KZFzniwnSyZfZ7v7nJ"
            } else if (cate == "Sports") {
                "KZFzniwnSyZfZ7v7nE"
            } else if (cate == "Arts & Theatre") {
                "KZFzniwnSyZfZ7v7na"
            } else if (cate == "Film") {
                "KZFzniwnSyZfZ7v7nn"
            } else if (cate == "Miscellaneous") {
                "KZFzniwnSyZfZ7v7n1"
            } else {
                ""
            }

            // Build the URL with the query parameters

            val queryParams = listOf(
                "keyword" to keywords,
                "category" to segid,
                "distance" to distance,
                "unit" to "miles"
            )
            var back_url =
                "$baseUrl?${queryParams.joinToString("&") { "${it.first}=${it.second}" }}"


            //get location
            if (autodetect.isChecked) {

                val queue = Volley.newRequestQueue(context)
                val url = "https://ipinfo.io/?token=d9b36329f4c023"
                val stringRequest = StringRequest(Request.Method.GET, url,
                    { response ->
                        val jsonData = JSONObject(response)
                        val latLng = jsonData.getString("loc").split(",")
                        val ipLat = latLng[0]
                        val ipLng = latLng[1]
                        back_url += "&lat=$ipLat&lon=$ipLng"
                        retrieveBackendData(back_url)

                    },
                    { error ->
                        Log.e("Volley Error", error.toString())
                    })

                queue.add(stringRequest)

            } else if (!autodetect.isChecked && location.isNotEmpty()) {
                val queue = Volley.newRequestQueue(context)
                val googleApi =
                    "https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyAGZgv5u9GctZka5uPh34u5VpLwYFbP1dA&address=$location"

                Log.e("Volley Error",googleApi)
                val stringRequest = StringRequest(Request.Method.GET, googleApi,
                    { response ->
                        val jsonData = JSONObject(response)
                        val latLng = jsonData.getJSONArray("results")
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                        val lat = latLng.getDouble("lat")
                        val lng = latLng.getDouble("lng")
                        back_url += "&lat=$lat&lon=$lng"
                        retrieveBackendData(back_url)
                    },
                    { error ->
                        Log.e("Volley Error", error.toString())
                    })
                queue.add(stringRequest)
            }
        }
        return view
    }

    fun retrieveBackendData(back_url: String) {
        val stringRequest = StringRequest(Request.Method.GET, back_url,
            { response ->

                val jsonObject = JSONObject(response)

                val totalElements = jsonObject.optJSONObject("page").optInt("totalElements", 0)
                if (totalElements == 0) {
                    Toast.makeText(context, "No search result", Toast.LENGTH_SHORT).show()
                }
                else {
                    val searchResultFragment = SearchResultFragment.newInstance(response)
                    val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(R.id.search_container, searchResultFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }

            },
            { error ->
                Log.e(TAG, "Error during retrieve backend", error)
            }
        )
        Volley.newRequestQueue(context).add(stringRequest)
    }




    private fun createTextWatcher(adapter: ArrayAdapter<String>): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val autoCompleteTextView = view?.findViewById<AutoCompleteTextView>(R.id.autocomplete_textview)
                val url = "https://app.ticketmaster.com/discovery/v2/suggest?apikey=xHeTjpTC2ATVMngYYTRkXkro82b5k2JS&keyword=$s"
                val request = JsonObjectRequest(
                    Request.Method.GET, url, null,
                    { response: JSONObject ->

                        val embeddedObj = response.optJSONObject("_embedded")
                        if (embeddedObj != null) {

                            val attractionsArray = embeddedObj.optJSONArray("attractions")
                            if (attractionsArray != null) {
                                for (i in 0 until attractionsArray.length()) {
                                    val attractionObj = attractionsArray.optJSONObject(i)
                                    val attractionName = attractionObj.optString("name")
                                    adapter.add(attractionName)

                                }
                            }
                        }
                        adapter.notifyDataSetChanged()
                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed({
                            autoCompleteTextView?.showDropDown()
                        }, 500)
                    },
                    { error: VolleyError? ->
                        Log.e(TAG, "Error during network request", error)
                    }
                )
                val queue = Volley.newRequestQueue(requireContext())
                queue.add(request)
            }
            override fun afterTextChanged(s: Editable) {}
        }
    }



    companion object {
        private const val TAG = "SearchFragment"
    }




}
