package com.github.iwle.ausm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var firestore: FirebaseFirestore

    companion object {
        fun newInstance(): MapFragment {
            return MapFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_map, container, false)
        setUpMap(v, savedInstanceState)
        return v
    }

    private fun setUpMap(v: View, savedInstanceState: Bundle?) {
        // Get MapView from the XML file and create it
        mapView = v.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        val position = LatLng(1.304734, 103.772420)
        googleMap.addMarker(MarkerOptions().position(position))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16.0f))

        // Hide app bar when map is selected
        googleMap.setOnCameraMoveStartedListener {
            activity?.findViewById<AppBarLayout>(R.id.app_bar_layout)?.setExpanded(false, true)
        }

        firestore = FirebaseFirestore.getInstance()
        firestore.collection("gps")
            .document("log")
            .addSnapshotListener(MetadataChanges.INCLUDE) { documentSnapshot, firebaseFirestoreException ->
                // TODO
            }
    }

    // MapView does not load unless being touched without overriding onResume()
    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }
}