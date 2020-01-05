package com.github.iwle.ausm

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
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
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import java.util.*

class EditReviewActivity : AppCompatActivity() {
    private val TAG: String = "DatabaseConnection"

    private lateinit var review: Review
    private lateinit var placeId: String
    private lateinit var userId: String

    private lateinit var noiseChipGroup: ChipGroup
    private lateinit var noiseChip1: Chip
    private lateinit var noiseChip2: Chip
    private lateinit var noiseChip3: Chip
    private lateinit var noiseChip4: Chip
    private lateinit var noiseChip5: Chip
    private lateinit var crowdChipGroup: ChipGroup
    private lateinit var crowdChip1: Chip
    private lateinit var crowdChip2: Chip
    private lateinit var crowdChip3: Chip
    private lateinit var crowdChip4: Chip
    private lateinit var crowdChip5: Chip
    private lateinit var editReviewButton: Button
    private lateinit var overallRatingBar: MaterialRatingBar
    private lateinit var reviewTextEdit: TextInputEditText
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_review)
        review = intent.getSerializableExtra("review") as Review
        placeId = intent.getStringExtra("placeId")
        userId = intent.getStringExtra("userId")

        noiseChipGroup = findViewById(R.id.noise_chip_group)
        noiseChip1 = findViewById(R.id.noise_chip_1)
        noiseChip2 = findViewById(R.id.noise_chip_2)
        noiseChip3 = findViewById(R.id.noise_chip_3)
        noiseChip4 = findViewById(R.id.noise_chip_4)
        noiseChip5 = findViewById(R.id.noise_chip_5)
        crowdChipGroup = findViewById(R.id.crowd_chip_group)
        crowdChip1 = findViewById(R.id.crowd_chip_1)
        crowdChip2 = findViewById(R.id.crowd_chip_2)
        crowdChip3 = findViewById(R.id.crowd_chip_3)
        crowdChip4 = findViewById(R.id.crowd_chip_4)
        crowdChip5 = findViewById(R.id.crowd_chip_5)
        editReviewButton = findViewById(R.id.edit_review_button)
        overallRatingBar = findViewById(R.id.overall_rating_bar)
        reviewTextEdit = findViewById(R.id.review_text_input)
        toolbar = findViewById(R.id.edit_review_toolbar)

        initialiseToolbar()
        populateData()
        initialiseButton()
    }

    private fun initialiseToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun populateData() {
        when(review.noiseRating) {
            1 -> noiseChip1.isSelected = true
            2 -> noiseChip2.isSelected = true
            3 -> noiseChip3.isSelected = true
            4 -> noiseChip4.isSelected = true
            5 -> noiseChip5.isSelected = true
        }
        when(review.crowdRating) {
            1 -> crowdChip1.isSelected = true
            2 -> crowdChip2.isSelected = true
            3 -> crowdChip3.isSelected = true
            4 -> crowdChip4.isSelected = true
            5 -> crowdChip5.isSelected = true
        }
        overallRatingBar.rating = review.overallRating.toFloat()
        reviewTextEdit.setText(review.review)
    }

    private fun initialiseButton() {
        editReviewButton.setOnClickListener {
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
            val reviewText: String = reviewTextEdit.text.toString()
            if(noiseRating == -1) {
                Toast.makeText(this, R.string.missing_choice_noise, Toast.LENGTH_LONG).show()
            } else if(crowdRating == -1) {
                Toast.makeText(this, R.string.missing_choice_crowd, Toast.LENGTH_LONG).show()
            } else if(overallRating == 0) {
                Toast.makeText(this, R.string.missing_rating_overall, Toast.LENGTH_LONG).show()
            } else {
                editReview(Review(review.userId, Calendar.getInstance().time, noiseRating, crowdRating, overallRating, reviewText))
            }
        }
    }

    private fun editReview(review2: Review) {
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val establishments: CollectionReference = firestore.collection("establishments")
        val reviews: CollectionReference = establishments
            .document(placeId)
            .collection("reviews")

        fun addReviewToEstablishment() {
            // Overwrite Review under Establishment.Reviews
            Log.i(TAG, "Successfully updated review")
            reviews.document(userId).set(review2)

            // Update aggregate data in Establishment
            val establishmentReference = establishments.document(placeId)
            firestore.runTransaction { transaction ->
                transaction.get(establishmentReference) // Somehow removing this line causes the next line to return NPE
                val establishment = transaction.get(establishmentReference).toObject(Establishment::class.java)!!

                val numReviews = establishment.numReviews
                val newNoiseRating = (establishment.noiseRating * numReviews - review.noiseRating + review2.noiseRating) / numReviews
                val newCrowdRating = (establishment.crowdRating * numReviews - review.crowdRating + review2.crowdRating) / numReviews
                val newOverallRating = (establishment.overallRating * numReviews - review.overallRating + review2.overallRating) / numReviews

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

        addReviewToEstablishment()
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