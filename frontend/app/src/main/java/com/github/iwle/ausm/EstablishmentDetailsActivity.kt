package com.github.iwle.ausm

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import com.github.iwle.ausm.model.Establishment

class EstablishmentDetailsActivity : Activity() {
    private lateinit var establishment: Establishment
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_establishment_details)

        establishment = intent.getSerializableExtra("establishment") as Establishment

        imageView = findViewById(R.id.image_view)

        initialiseDetails()
    }

    private fun initialiseDetails() {
        // Decode Base64 String to Bitmap
        val imageBytes = Base64.decode(establishment.imageBase64, Base64.URL_SAFE)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        imageView.setImageBitmap(decodedImage)
    }
}