package com.github.iwle.ausm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.appbar.AppBarLayout

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView

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
        googleMap.setOnCameraMoveStartedListener {
            activity?.findViewById<AppBarLayout>(R.id.app_bar_layout)?.setExpanded(false, true)
        }
    }

    // MapView does not load unless being touched without overriding onResume()
    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }
}