package com.example.placessearch

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

/**
 * @author leoul.
 * PlaceAutoCompleteAdapter.
 * more info: https://developers.google.com/maps/documentation/places/android-sdk/client-migration for detail
 */
class PlaceAutoCompleteAdapter(
    val placesClient: PlacesClient,
    val req: FindAutocompletePredictionsRequest.Builder,
    val searchEditTxt: EditText,
) : RecyclerView.Adapter<PlaceAutoCompleteAdapter.ViewHolder>(), Filterable {
    private var addressList: List<AutocompletePrediction>? = null
    private var lastSelectedAddr = ""

    override fun getFilter() = filter

    private val filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            val inputAddr = constraint.toString()
            // no need to query address if the input is already in addressList
            if (inputAddr != lastSelectedAddr) {
                val placeQuery = req.setQuery(inputAddr).build()

                placesClient.findAutocompletePredictions(placeQuery)
                    .addOnSuccessListener {
                        addressList = it.autocompletePredictions
                        notifyDataSetChanged()
                        filterResults.values = addressList
                        filterResults.count = addressList?.size!!
                    }
                    .addOnFailureListener {
                        // you can display error message to the user here
                        Log.e(TAG, "Place not found: " + (it as ApiException).statusCode)
                    }
            }
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results != null && results.count > 0) {
                notifyDataSetChanged()
            }
        }
    }

    /**
     * delete addressList. This removes address result list if one address is chosen/clicked
     */
    fun clearPlacesResult() {
        addressList = null
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.place_cell, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.address.text = addressList?.get(position)?.getFullText(null)
    }

    override fun getItemCount() = if (addressList.isNullOrEmpty()) 0 else addressList!!.size

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val address: TextView = v.findViewById(R.id.address)

        init {
            address.setOnClickListener {  //  click listener for the ViewHolder's View.
                val addr = address.text.toString()
                // Note: updating lastSelectedAddr before setting searchEditTxt value is necessary
                // so that the TextWatcher is called after updating the lastSelectedAddr so
                // that the exact address is not queried again
                lastSelectedAddr = addr
                searchEditTxt.clearFocus() // this helps to hide soft keyboard
                clearPlacesResult() // address is chosen so remove the address list
                searchEditTxt.setText(addr) // set the selected address to inputField
            }
        }
    }

    companion object {
        private const val TAG = "PlaceAutoCompleteAdapter"
    }
}