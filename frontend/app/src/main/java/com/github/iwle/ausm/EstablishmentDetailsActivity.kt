package com.github.iwle.ausm

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.iwle.ausm.adapter.ReviewAdapter
import com.github.iwle.ausm.model.Establishment
import com.github.iwle.ausm.model.Info
import com.github.iwle.ausm.model.Review
import com.github.iwle.ausm.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EstablishmentDetailsActivity : AppCompatActivity() {
    private lateinit var establishment: Establishment
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var collapsingToolbarLayout: net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout
    private lateinit var toolbar: Toolbar
    private lateinit var imageView: ImageView
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var reviewList: ArrayList<Review>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_establishment_details)

        establishment = intent.getSerializableExtra("establishment") as Establishment
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout)
        toolbar = findViewById(R.id.toolbar)
        imageView = findViewById(R.id.image_view)
        floatingActionButton = findViewById(R.id.floating_action_button)

        reviewList = ArrayList()
        reviewAdapter = ReviewAdapter(Info(
            establishment.address,
            establishment.openingHours,
            establishment.phoneNumber,
            establishment.websiteUrl), reviewList)
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = reviewAdapter
        }

        initialiseDetails()
        fetchData()
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

        setFabAction()

        // Decode Base64 String to Bitmap
        val imageBytes = Base64.decode(establishment.imageBase64, Base64.URL_SAFE)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        imageView.setImageBitmap(decodedImage)

        // Set toolbar title
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        collapsingToolbarLayout.title = establishment.name
    }

    private fun fetchData() {
        firestore.collection("establishments")
            .document(establishment.placeId)
            .collection("reviews")
            .get()
            .addOnSuccessListener { querySnapshot ->
                reviewList.clear()
                for(documentSnapshot in querySnapshot) {
                    val review = documentSnapshot.toObject(Review::class.java)
                    reviewList.add(review)
                }

                // Update reviewAdapter
                reviewAdapter.notifyDataSetChanged()
            }
    }
}