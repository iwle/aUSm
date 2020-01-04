package com.github.iwle.ausm.model

import com.google.android.libraries.places.api.model.Place
import java.io.Serializable

data class Establishment(
    var placeId: String = "",
    var name: String = "",
    var address: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var imageBase64: String = "",
    var phoneNumber: String = "",
    var websiteUrl: String = "",
    var openingHours: String = "",
    var placeType: List<Place.Type> = ArrayList(),
    var numReviews: Int = 0,
    var noiseRating: Float = 0f,
    var crowdRating: Float = 0f,
    var overallRating: Float = 0f
) : Serializable