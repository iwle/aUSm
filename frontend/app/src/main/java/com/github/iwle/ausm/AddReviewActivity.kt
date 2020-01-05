package com.github.iwle.ausm

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
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
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

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
    private lateinit var firebaseAuth: FirebaseAuth

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

        firebaseAuth = FirebaseAuth.getInstance()
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
                addReview(Review(firebaseAuth.uid!!, Calendar.getInstance().time, noiseRating, crowdRating, overallRating, review))
            }
        }
    }

    private fun addReview(review: Review) {
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val establishments: CollectionReference = firestore.collection("establishments")
        val reviews: CollectionReference = establishments
            .document(place.id!!)
            .collection("reviews")
        val users: CollectionReference = firestore.collection("users")

        fun addReviewToEstablishment() {
            // Create new Review under Establishment.Reviews
            Log.i(TAG, "Successfully created new Review under Establishment")
            reviews.document(firebaseAuth.uid!!).set(review)

            // Add Establishment reference to User
            Log.i(TAG,"Successfully added reference to Establishment to User")
            users.document(firebaseAuth.uid!!).update("reviewsList", FieldValue.arrayUnion(place.id!!))

            // Update aggregate data in Establishment
            val establishmentReference = establishments.document(place.id!!)
            firestore.runTransaction { transaction ->
                transaction.get(establishmentReference) // Somehow removing this line causes the next line to return NPE
                val establishment = transaction.get(establishmentReference).toObject(Establishment::class.java)!!

                val oldNumReviews = establishment.numReviews
                val newNumReviews = oldNumReviews + 1
                val oldNoiseRatingTotal = establishment.noiseRating * oldNumReviews
                val newNoiseRating = (oldNoiseRatingTotal + review.noiseRating) / newNumReviews
                val oldCrowdRatingTotal = establishment.crowdRating * oldNumReviews
                val newCrowdRating = (oldCrowdRatingTotal + review.crowdRating) / newNumReviews
                val oldOverallRatingTotal = establishment.overallRating * oldNumReviews
                val newOverallRating = (oldOverallRatingTotal + review.overallRating) / newNumReviews

                establishment.numReviews = newNumReviews
                establishment.noiseRating = newNoiseRating
                establishment.crowdRating = newCrowdRating
                establishment.overallRating = newOverallRating

                transaction.set(establishmentReference, establishment)
            }.addOnSuccessListener {
                Log.i(TAG, "Successfully updated aggregate data in Establishment")
            }.addOnFailureListener { exception ->
                // Check Firestore update permissions
                Log.e(TAG, "Failed to update aggregate data in Establishment: ", exception)
            }

            // Redirect user
            Toast.makeText(this, R.string.add_review_success, Toast.LENGTH_LONG).show()
            finish()
        }

        fun initialiseEstablishment() {
            fun createEstablishment(imageB64: String = "") {
                Log.i(TAG, "Successfully created new Establishment")

                val placeFields = arrayListOf<Place.Field>(
                    Place.Field.PHONE_NUMBER,
                    Place.Field.WEBSITE_URI,
                    Place.Field.OPENING_HOURS)
                val fetchPlaceRequest = FetchPlaceRequest.newInstance(place.id!!, placeFields)
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener { fetchPlaceResponse ->
                    val place2 = fetchPlaceResponse.place
                    var openingHours = ""
                    if(place2.openingHours != null) {
                        openingHours = place2.openingHours!!.weekdayText.toString()
                    }

                    establishments.document(place.id!!).set(
                        Establishment(
                            place.id!!,
                            place.name!!,
                            place.address!!,
                            place.latLng!!.latitude,
                            place.latLng!!.longitude,
                            imageB64,
                            place2.phoneNumber ?: "",
                            (place2.websiteUri ?: "").toString(),
                            openingHours,
                            place.types ?: ArrayList()
                        )
                    )
                    addReviewToEstablishment()
                }
            }

            // Check if Establishment exists in database
            establishments.document(place.id!!).get()
                .addOnCompleteListener {task ->
                    if(task.isSuccessful) {
                        val document: DocumentSnapshot = task.result!!
                        if(document.exists()) {
                            // Establishment exists in database
                            Log.i(TAG, "Establishment exists in database")
                            addReviewToEstablishment()
                        } else {
                            Log.i(TAG, "Establishment does not exist in database")
                            var imageB64: String
                            if(place.photoMetadatas != null) {
                                // Get establishment photo
                                val photoMetadata: PhotoMetadata = place.photoMetadatas!![0]
                                val photoRequest: FetchPhotoRequest =
                                    FetchPhotoRequest.builder(photoMetadata)
                                        .setMaxHeight(400)
                                        .setMaxWidth(400)
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

                                        // Create establishment
                                        createEstablishment(imageB64)
                                    }
                            } else {
                                // No photo - use static map instead
                                val PLACE_IMG_WIDTH = 640
                                val PLACE_IMG_HEIGHT = 500
                                val STATIC_MAP_URL = "https://maps.googleapis.com/maps/api/staticmap?" +
                                        "size=${PLACE_IMG_WIDTH}x$PLACE_IMG_HEIGHT" +
                                        "&markers=color:red|%.6f,%.6f" +
                                        "&key=%s"
                                val mapUrl = STATIC_MAP_URL
                                    .format(place.latLng!!.latitude,
                                        place.latLng!!.longitude,
                                        resources.getString(R.string.place_picker_maps_key))
                                Picasso.get().load(mapUrl).into(object : com.squareup.picasso.Target {
                                    override fun onBitmapLoaded(
                                        bitmap: Bitmap?,
                                        from: Picasso.LoadedFrom?
                                    ) {
                                        // Encode image to Base64
                                        val byteArrayOutputStream = ByteArrayOutputStream()
                                        bitmap!!.compress(
                                            Bitmap.CompressFormat.PNG,
                                            100,
                                            byteArrayOutputStream
                                        )
                                        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                                        imageB64 = Base64.encodeToString(byteArray, Base64.URL_SAFE)

                                        createEstablishment(imageB64)
                                    }

                                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                                    override fun onBitmapFailed(
                                        e: Exception?,
                                        errorDrawable: Drawable?
                                    ) {}
                                })
                            }
                        }
                    } else {
                        Log.e(TAG, "Firestore query failed: ", task.exception)
                    }
                }
                .addOnFailureListener { exception ->
                    // Failed to query database
                    Log.e(TAG, "Failed to contact Firestore: ", exception)
                    Toast.makeText(this, R.string.connection_failed, Toast.LENGTH_LONG).show()
                }
        }

        fun checkIfReviewExists() {
            reviews.document(firebaseAuth.uid!!).get()
                .addOnCompleteListener {task ->
                    if(task.isSuccessful) {
                        val document: DocumentSnapshot = task.result!!
                        if(document.exists()) {
                            // Review already exists
                            Log.i(TAG, "Review for Establishment already exists in database")
                            Toast.makeText(this, R.string.review_already_exists, Toast.LENGTH_LONG).show()
                        } else {
                            // Review does not exist
                            Log.i(TAG, "Review for Establishment does not exist in databse")
                            initialiseEstablishment()
                        }
                    } else {
                        Log.i(TAG, "Firestore query failed: ", task.exception)
                    }
                }
        }

        checkIfReviewExists()
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