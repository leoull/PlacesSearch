package com.example.placessearch

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.Places

class MainActivity : AppCompatActivity() {
    private lateinit var clearImgV: ImageView
    private lateinit var placesAdapter: PlaceAutoCompleteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clearImgV = findViewById(R.id.clear)

        Places.initialize(this, "add google_maps_key")
        val mPlacesClient = Places.createClient(this)

        findViewById<RecyclerView>(R.id.placesRcyView).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            placesAdapter = PlaceAutoCompleteAdapter(context, mPlacesClient)
            adapter = placesAdapter

        }
        findViewById<EditText>(R.id.search_et).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if (count > 0) {
//                    clearImgV.visibility = View.VISIBLE;
//                    if (placesAdapter != null) {
//                        mRecyclerView.setAdapter(mAdapter);
//                    }
//                } else {
//                    clearImgV.visibility = View.GONE;
//                    if (mSavedAdapter != null && mSavedAddressList.size() > 0) {
//                        mRecyclerView.setAdapter(mSavedAdapter);
//                    }
//                }
//                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
//                    mAdapter.getFilter().filter(s.toString());
//                } else if (!mGoogleApiClient.isConnected()) {
////                    Toast.makeText(getApplicationContext(), Constants.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
//                    Log.e("", "NOT CONNECTED");
//                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    clearImgV.visibility = View.GONE
                } else {
                    clearImgV.visibility = View.VISIBLE
                    placesAdapter.filter.filter(s.toString());
                }
            }

        })

    }


}


