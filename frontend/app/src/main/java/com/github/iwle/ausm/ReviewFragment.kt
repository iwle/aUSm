package com.github.iwle.ausm

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.iwle.ausm.adapter.EstablishmentAdapter
import com.github.iwle.ausm.comparator.EstablishmentLocationComparator
import com.github.iwle.ausm.model.Establishment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class ReviewFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var recyclerView: RecyclerView
    private lateinit var establishmentAdapter: EstablishmentAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var firestore: FirebaseFirestore
    private lateinit var establishmentList: ArrayList<Establishment>
    private lateinit var establishments: CollectionReference

    companion object {
        private val PERMISSION_REQUEST_ACCESS_LOCATION = 100
        fun newInstance() : ReviewFragment {
            return ReviewFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestore = FirebaseFirestore.getInstance()
        establishments = firestore.collection("establishments")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.activity as Activity)

        val v = inflater.inflate(R.layout.fragment_review, container, false)
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener(this)
        // Set refresh animation colour cycle
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorSecondary)
        // Initialise reviewListAdapter
        establishmentList = ArrayList()
        establishmentAdapter = EstablishmentAdapter(establishmentList) { establishment: Establishment ->
            onEstablishmentClicked(establishment)
        }
        fetchData()
        recyclerView = v.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = establishmentAdapter
        }
        return v
    }

    private fun onEstablishmentClicked(establishment: Establishment) {
        val intent: Intent = Intent(this.activity, EstablishmentDetailsActivity::class.java)
        intent.putExtra("establishment", establishment)
        startActivity(intent)
    }

    private fun fetchData() {
        establishments.get().addOnSuccessListener { querySnapshot ->
            establishmentList.clear()
            for(documentSnapshot in querySnapshot) {
                val establishment = documentSnapshot.toObject(Establishment::class.java)
                establishmentList.add(establishment)
            }

            getLastLocation()
        }
    }

    override fun onRefresh() {
        fetchData()
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermissions() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation.addOnCompleteListener(this.activity as Activity) { task ->
                    var location: Location? = task.result
                    if(location != null) {
                        // Sort establishmentList by distance from current location
                        establishmentList.sortWith(EstablishmentLocationComparator(LatLng(location.latitude, location.longitude)))

                        // Update establishmentAdapter
                        establishmentAdapter.notifyDataSetChanged()

                        // Stop the refresh animation
                        val runnable = Runnable {
                            if(swipeRefreshLayout.isRefreshing) {
                                swipeRefreshLayout.isRefreshing = false
                            }
                        }
                        Handler().postDelayed(runnable, 1000)
                    }
                }
            } else {
                Toast.makeText(this.context, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            if(swipeRefreshLayout.isRefreshing) {
                swipeRefreshLayout.isRefreshing = false
            }
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == PERMISSION_REQUEST_ACCESS_LOCATION && checkPermissions()) {
            swipeRefreshLayout.isRefreshing = true
            fetchData()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}