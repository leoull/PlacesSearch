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
 * Refere https://developers.google.com/maps/documentation/places/android-sdk/client-migration for detail
 */
class PlaceAutoCompleteAdapter(
    val placesClient: PlacesClient,
    val req: FindAutocompletePredictionsRequest.Builder,
    val searchEditTxt: EditText,
) : RecyclerView.Adapter<PlaceAutoCompleteAdapter.ViewHolder>(), Filterable {
    private var addressList: List<AutocompletePrediction>? = null

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val inputAddress = constraint.toString()
                // no need to query address if the input is already in addressList
                if (constraint != null && !alreadyQueried(inputAddress)) {
                    val placeQuery = req.setQuery(inputAddress).build()

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
                } else if (constraint.toString().equals(searchEditTxt.text)) {
                    clearPlacesResult()
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                }
            }
        }
    }

    /**
     * check if the resultsList contains the input address
     */
    private fun alreadyQueried(currentAddress: String): Boolean {
        addressList?.forEach {
            if (currentAddress == it.getFullText(null).toString()) {
                return true
            }
        }
        return false
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
                searchEditTxt.setText(address.text) // set the selected address to inputField
                searchEditTxt.clearFocus() // this helps to hide soft keyboard
                clearPlacesResult() // address is chosen so remove the address list
            }
        }
    }

    companion object {
        private const val TAG = "PlaceAutoCompleteAdapter"
    }
}