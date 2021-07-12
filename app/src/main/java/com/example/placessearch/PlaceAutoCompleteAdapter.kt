package com.example.placessearch

import android.graphics.Typeface
import android.text.style.StyleSpan
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
    val placesRcyView: RecyclerView
) : RecyclerView.Adapter<PlaceAutoCompleteAdapter.ViewHolder>(), Filterable {
    private var resultList: List<AutocompletePrediction>? = null

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val locReq = req.setQuery(constraint.toString()).build()

                    placesClient.findAutocompletePredictions(locReq)
                        .addOnSuccessListener {
                            resultList = it.autocompletePredictions
                            notifyDataSetChanged()
                            filterResults.values = resultList
                            filterResults.count = resultList?.size!!
                        }
                        .addOnFailureListener {
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.place_cell, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.place.text = resultList?.get(position)?.getFullText(StyleSpan(Typeface.NORMAL))
    }

    override fun getItemCount() = if (resultList.isNullOrEmpty()) 0 else resultList!!.size

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val place: TextView = v.findViewById(R.id.address)

        init {
            place.setOnClickListener {  //  click listener for the ViewHolder's View.
                searchEditTxt.setText(place.text)
                searchEditTxt.clearFocus()
                placesRcyView.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val TAG = "PlaceAutoCompleteAdapter"
    }
}