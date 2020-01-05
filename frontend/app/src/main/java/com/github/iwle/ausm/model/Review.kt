package com.github.iwle.ausm.model

import java.util.*

data class Review(
    var userId: String = "",
    var date: Date = Date(),
    var noiseRating: Int = 0,
    var crowdRating: Int = 0,
    var overallRating: Int = 0,
    var review: String = ""
)