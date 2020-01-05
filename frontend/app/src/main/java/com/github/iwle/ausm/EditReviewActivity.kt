package com.github.iwle.ausm

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.iwle.ausm.model.Review
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import me.zhanghai.android.materialratingbar.MaterialRatingBar

class EditReviewActivity : AppCompatActivity() {
    private lateinit var review: Review
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
    private lateinit var addReviewButton: Button
    private lateinit var overallRatingBar: MaterialRatingBar
    private lateinit var reviewTextEdit: TextInputEditText
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_review)
        review = intent.getSerializableExtra("review") as Review

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
        addReviewButton = findViewById(R.id.edit_review_button)
        overallRatingBar = findViewById(R.id.overall_rating_bar)
        reviewTextEdit = findViewById(R.id.review_text_input)
        toolbar = findViewById(R.id.edit_review_toolbar)

        initialiseToolbar()
        populateData()
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