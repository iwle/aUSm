package com.github.iwle.ausm

import android.content.Intent
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
import com.github.iwle.ausm.model.EstablishmentDetails
import com.github.iwle.ausm.model.Review
import com.github.iwle.ausm.model.User
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
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
    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_establishment_details)

        establishment = intent.getSerializableExtra("establishment") as Establishment
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        Places.initialize(applicationContext, getString(R.string.place_picker_places_key))
        placesClient = Places.createClient(this)

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout)
        toolbar = findViewById(R.id.toolbar)
        imageView = findViewById(R.id.image_view)
        floatingActionButton = findViewById(R.id.floating_action_button)

        reviewList = ArrayList()
        reviewAdapter = ReviewAdapter(EstablishmentDetails(
            establishment.overallRating,
            establishment.noiseRating,
            establishment.crowdRating,
            establishment.numReviews,
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
                        floatingActionButton.setOnClickListener {
                            firestore.collection("establishments")
                                .document(establishment.placeId)
                                .collection("reviews")
                                .document(firebaseAuth.uid!!)
                                .get()
                                .addOnSuccessListener { documentSnapshot ->
                                    val intent = Intent(this, EditReviewActivity::class.java)
                                    val review = documentSnapshot.toObject(Review::class.java)
                                    intent.putExtra("review", review)
                                    intent.putExtra("placeId", establishment.placeId)
                                    intent.putExtra("userId", firebaseAuth.uid)
                                    startActivity(intent)
                                }
                        }
                    } else {
                        // Add mode
                        floatingActionButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_add_white_24))
                        floatingActionButton.setOnClickListener {
                            val placeFields = arrayListOf<Place.Field>(
                                Place.Field.ID,
                                Place.Field.ADDRESS,
                                Place.Field.LAT_LNG,
                                Place.Field.NAME,
                                Place.Field.PHOTO_METADATAS,
                                Place.Field.TYPES)
                            val fetchPlaceRequest = FetchPlaceRequest.newInstance(establishment.placeId, placeFields)
                            placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener { fetchPlaceResponse ->
                                val place = fetchPlaceResponse.place
                                val intent = Intent(this, AddReviewActivity::class.java)
                                intent.putExtra("place", place)
                                startActivity(intent)
                            }
                        }
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
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
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