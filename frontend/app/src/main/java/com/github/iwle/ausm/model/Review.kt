package com.github.iwle.ausm.model

data class Review(
    val noiseRating: Int,
    val crowdRating: Int,
    val overallRating: Int,
    val review: String
)