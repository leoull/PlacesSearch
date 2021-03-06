package com.example.placessearch

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest

/**
 * @author leoul
 * custom PlaceAutoComplete. refer https://developers.google.com/maps/documentation/places/android-sdk/client-migration for more detail
 */
class MainActivity : AppCompatActivity() {
    private lateinit var placesAdapter: PlaceAutoCompleteAdapter
    private lateinit var clearResults: ImageView
    private lateinit var placesRcyView: RecyclerView
    private lateinit var placesInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        TODO("add api key here")
        Places.initialize(this, "places_api_key")
        val placesClient = Places.createClient(this)
        val token = AutocompleteSessionToken.newInstance()
        val req = FindAutocompletePredictionsRequest.builder()
            .setCountry("US")
            .setTypeFilter(TypeFilter.ADDRESS)
            .setSessionToken(token)

        clearResults = findViewById(R.id.clearImgView); clearResults.setOnClickListener(clkListener)

        placesInput = findViewById(R.id.search_et)
        placesInput.addTextChangedListener(txtWatcher)
        placesInput.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(view)
            }
        }

        placesRcyView = findViewById(R.id.placesRcyView)
        placesRcyView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            placesAdapter = PlaceAutoCompleteAdapter(placesClient, req, placesInput)
            adapter = placesAdapter
        }
    }

    private val txtWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (s.isNullOrBlank()) {
                clearResults.visibility = View.GONE // hide clear button
            } else {
                placesAdapter.filter.filter(s.toString()) // filter the input query
                clearResults.visibility = View.VISIBLE // show clear button
            }
        }
    }
    private val clkListener = View.OnClickListener {
        when (it.id) {
            R.id.clearImgView -> {
                placesAdapter.clearPlacesResult() // remove address list
                placesInput.text.clear() // clear input text
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}


