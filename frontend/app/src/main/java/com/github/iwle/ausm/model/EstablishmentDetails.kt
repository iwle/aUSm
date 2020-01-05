package com.github.iwle.ausm.model

data class EstablishmentDetails(
    var overallRating: Float,
    var noiseRating: Float,
    var crowdRating: Float,
    var numRatings: Int,
    var address: String,
    var openingHours: String,
    var phoneNumber: String,
    var website: String
)