package com.github.iwle.ausm.model

data class Establishment(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val imageBase64: String = "",
    val noiseRating: Float = 0f,
    val soundRating: Float = 0f,
    val rating: Float = 0f
)