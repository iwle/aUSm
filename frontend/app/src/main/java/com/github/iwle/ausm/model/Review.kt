package com.github.iwle.ausm.model

data class Review(
    var userId: String = "",
    var noiseRating: Int = 0,
    var crowdRating: Int = 0,
    var overallRating: Int = 0,
    var review: String = ""
)