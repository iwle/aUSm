package com.github.iwle.ausm

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.github.iwle.ausm.model.Establishment
import com.github.iwle.ausm.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EstablishmentDetailsActivity : Activity() {
    private lateinit var establishment: Establishment
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var imageView: ImageView
    private lateinit var floatingActionButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_establishment_details)

        establishment = intent.getSerializableExtra("establishment") as Establishment
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        imageView = findViewById(R.id.image_view)
        floatingActionButton = findViewById(R.id.floating_action_button)

        initialiseDetails()
    }

    private fun initialiseDetails() {
        fun setFabAction() {
            val users = firestore.collection("users")
            users.document(firebaseAuth.uid!!)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject(User::class.java)!!
                    if(user.reviewsList.contains(establishment.placeId)) {
                        // Edit mode
                        floatingActionButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_edit_white_24))
                    } else {
                        // Add mode
                        floatingActionButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_add_white_24))
                    }
                }
        }

        // Decode Base64 String to Bitmap
        val imageBytes = Base64.decode(establishment.imageBase64, Base64.URL_SAFE)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        imageView.setImageBitmap(decodedImage)

        setFabAction()
    }
}