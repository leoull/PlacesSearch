package com.example.placessearch

import android.content.Context
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.placessearch.util.getAutocomplete
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.PlacesClient

/**
 * @author leoul.
 * TODO Enter description
 */
class PlaceAutoCompleteAdapter(
    val context: Context, val mPlacesClient: PlacesClient
) : RecyclerView.Adapter<PlaceAutoCompleteAdapter.ViewHolder>(), Filterable {
    private var mContext: Context = context
   private var resultList = arrayListOf<String>()
//    private var resultList:List<AutocompletePrediction>


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    resultList.clear()
                    val addressList = getAutocomplete(mPlacesClient, constraint.toString())
                    addressList.let {
//                        resultList = it
                        for (i in addressList.indices) {
                            val item = addressList[i]
                            resultList.add(item.getFullText(StyleSpan(Typeface.BOLD)).toString())

//                            resultList.add(PlaceDataModel(item.placeId, item.getFullText(
//                                StyleSpan(
//                                Typeface.BOLD)
//                            ).toString()))
                        }
                    }
                    filterResults.values = resultList
                    filterResults.count = resultList.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
//                    context.runOnUiThread {
                    notifyDataSetChanged()
//                    }
                } else {

                    //notifyDataSetInvalidated() // TODO ???
                }
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.place_cell, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.place.text = resultList[position]
    }

    override fun getItemCount() = if (resultList.isNullOrEmpty()) 0 else resultList.size

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val place: TextView = v.findViewById(R.id.address)

        init {
            v.setOnClickListener {  //  click listener for the ViewHolder's View.

            }
        }
    }
}