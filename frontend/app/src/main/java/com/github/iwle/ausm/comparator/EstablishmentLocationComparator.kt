package com.github.iwle.ausm.comparator

import com.github.iwle.ausm.model.Establishment
import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

class EstablishmentLocationComparator(val latLng: LatLng) : Comparator<Establishment> {
    override fun compare(establishment1: Establishment, establishment2: Establishment): Int {
        val lat1 = establishment1.latitude
        val lng1 = establishment1.longitude
        val lat2 = establishment2.latitude
        val lng2 = establishment2.longitude

        val distanceToEstablishment1 = distance(latLng.latitude, latLng.longitude, lat1, lng1)
        val distanceToEstablishment2 = distance(latLng.latitude, latLng.longitude, lat2, lng2)
        return (distanceToEstablishment1 - distanceToEstablishment2).toInt()
    }

    private fun distance(fromLat: Double, fromLng: Double, toLat: Double, toLng: Double): Double {
        val radius = 6378137.0
        val deltaLat = toLat - fromLat
        val deltaLng = toLng - fromLng
        val angle = 2 * asin(sqrt(sin(deltaLat / 2).pow(2))) + cos(fromLat) * cos(toLat) * sin(deltaLng / 2).pow(2)
        return radius * angle
    }
}