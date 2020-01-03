package com.github.iwle.ausm

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.iwle.ausm.model.Establishment
import com.github.iwle.ausm.model.Review
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import java.io.ByteArrayOutputStream

class AddReviewActivity : AppCompatActivity() {
    private val TAG: String = "DatabaseConnection"

    private lateinit var place: Place
    private lateinit var noiseChipGroup: ChipGroup
    private lateinit var crowdChipGroup: ChipGroup
    private lateinit var addReviewButton: Button
    private lateinit var overallRatingBar: MaterialRatingBar
    private lateinit var reviewTextEdit: TextInputEditText
    private lateinit var toolbar: Toolbar
    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)

        place = intent.getParcelableExtra("place")
        noiseChipGroup = findViewById(R.id.noise_chip_group)
        crowdChipGroup = findViewById(R.id.crowd_chip_group)
        addReviewButton = findViewById(R.id.add_review_button)
        overallRatingBar = findViewById(R.id.overall_rating_bar)
        reviewTextEdit = findViewById(R.id.review_text_input)
        toolbar = findViewById(R.id.add_review_toolbar)

        Places.initialize(applicationContext, getString(R.string.place_picker_places_key))
        placesClient = Places.createClient(this)

        initialiseToolbar()
        initialiseButton()
    }

    private fun initialiseToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initialiseButton() {
        addReviewButton.setOnClickListener {
            var noiseRating: Int = -1
            when(noiseChipGroup.checkedChipId) {
                R.id.noise_chip_1 -> noiseRating = 1
                R.id.noise_chip_2 -> noiseRating = 2
                R.id.noise_chip_3 -> noiseRating = 3
                R.id.noise_chip_4 -> noiseRating = 4
                R.id.noise_chip_5 -> noiseRating = 5
                // No selection
                -1 -> noiseRating = -1
            }
            var crowdRating: Int = -1
            when(crowdChipGroup.checkedChipId) {
                R.id.crowd_chip_1 -> crowdRating = 1
                R.id.crowd_chip_2 -> crowdRating = 2
                R.id.crowd_chip_3 -> crowdRating = 3
                R.id.crowd_chip_4 -> crowdRating = 4
                R.id.crowd_chip_5 -> crowdRating = 5
                // No selection
                -1 -> crowdRating = -1
            }
            val overallRating: Int = overallRatingBar.rating.toInt()
            val review: String = reviewTextEdit.text.toString()
            if(noiseRating == -1) {
                Toast.makeText(this, R.string.missing_choice_noise, Toast.LENGTH_LONG).show()
            } else if(crowdRating == -1) {
                Toast.makeText(this, R.string.missing_choice_crowd, Toast.LENGTH_LONG).show()
            } else if(overallRating == 0) {
                Toast.makeText(this, R.string.missing_rating_overall, Toast.LENGTH_LONG).show()
            } else {
                addReview(Review(noiseRating, crowdRating, overallRating, review))
            }
        }
    }

    private fun addReview(review: Review) {
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

        // Check if establishment exists in database
        val establishments: CollectionReference = firestore.collection("establishments")
        establishments.document(place.id!!).get()
            .addOnCompleteListener {task ->
                if(task.isSuccessful) {
                    val document: DocumentSnapshot = task.result!!
                    if (document.exists()) {
                        // Establishment exists in database
                        Log.i(TAG, "Establishment exists in database")
                    } else {
                        Log.i(TAG, "Establishment does not exist in database")
                        var imageB64: String = ""
                        // Get establishment photo
                        val photoMetadata: PhotoMetadata = place.photoMetadatas!![0]
                        val photoRequest: FetchPhotoRequest =
                            FetchPhotoRequest.builder(photoMetadata)
                                .setMaxHeight(500)
                                .setMaxWidth(500)
                                .build()
                        placesClient.fetchPhoto(photoRequest)
                            .addOnSuccessListener { fetchPhotoResponse ->
                                Log.i(TAG, "Successfully fetched Google Place photo")
                                val bitmap: Bitmap = fetchPhotoResponse.bitmap

                                // Encode image to Base64
                                val byteArrayOutputStream = ByteArrayOutputStream()
                                bitmap.compress(
                                    Bitmap.CompressFormat.PNG,
                                    100,
                                    byteArrayOutputStream
                                )
                                bitmap.recycle()
                                val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                                imageB64 = Base64.encodeToString(byteArray, Base64.URL_SAFE)
                            }
                        // Create establishment
                        Log.i(TAG, "Successfully created new establishment")
                        establishments.document(place.id!!).set(
                            Establishment(
                                place.name!!,
                                place.address!!,
                                place.latLng!!.latitude,
                                place.latLng!!.longitude,
                                imageB64
                            )
                        )
                    }
                } else {
                    Log.i(TAG, "Firestore query failed: ", task.exception)
                }
            }
            .addOnFailureListener {
                // Failed to query database
                Log.i(TAG, "Failed to contact Firestore")
                Toast.makeText(this, R.string.connection_failed, Toast.LENGTH_LONG).show()
            }
        val reviews: CollectionReference = firestore.collection("reviews")
        // reviews.document(place.id!!).set(review)
    }

    // Hide keyboard when focus is lost
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}