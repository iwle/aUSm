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
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import me.zhanghai.android.materialratingbar.MaterialRatingBar

class EditReviewActivity : AppCompatActivity() {
    private lateinit var review: Review
    private lateinit var noiseChipGroup: ChipGroup
    private lateinit var crowdChipGroup: ChipGroup
    private lateinit var addReviewButton: Button
    private lateinit var overallRatingBar: MaterialRatingBar
    private lateinit var reviewTextEdit: TextInputEditText
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_review)
        review = intent.getSerializableExtra("review") as Review

        noiseChipGroup = findViewById(R.id.noise_chip_group)
        crowdChipGroup = findViewById(R.id.crowd_chip_group)
        addReviewButton = findViewById(R.id.edit_review_button)
        overallRatingBar = findViewById(R.id.overall_rating_bar)
        reviewTextEdit = findViewById(R.id.review_text_input)
        toolbar = findViewById(R.id.edit_review_toolbar)

        initialiseToolbar()
    }

    private fun initialiseToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
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