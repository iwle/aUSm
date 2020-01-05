package com.github.iwle.ausm.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.iwle.ausm.R
import com.github.iwle.ausm.model.Info
import com.github.iwle.ausm.model.Review
import com.google.common.math.DoubleMath.roundToInt
import com.google.firebase.firestore.FirebaseFirestore
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class ReviewAdapter (
    private val info: Info,
    private val reviews: ArrayList<Review>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    object Type {
        const val HEADER = 0
        const val REVIEW = 1
    }

    class HeaderViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val address: TextView = v.findViewById(R.id.info_address)
        val openingHours: TextView = v.findViewById(R.id.info_opening_hours)
        val phoneNumber: TextView = v.findViewById(R.id.info_phone_number)
        val website: TextView = v.findViewById(R.id.info_website)

        val callButton: LinearLayout = v.findViewById(R.id.button_call)
        val directionsButton: LinearLayout = v.findViewById(R.id.button_directions)
        val websiteButton: LinearLayout = v.findViewById(R.id.button_website)

        val overallRating: TextView = v.findViewById(R.id.overall_rating_text_view)
        val overallRatingBar: MaterialRatingBar = v.findViewById(R.id.overall_rating_bar)
        val numberRatings: TextView = v.findViewById(R.id.number_ratings_text_view)
        val noiseRating: TextView = v.findViewById(R.id.noise_rating_text_view)
        val crowdRating: TextView = v.findViewById(R.id.crowd_rating_text_view)
    }

    class ReviewViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.name_text_view)
        val ratingBar: MaterialRatingBar = v.findViewById(R.id.rating_bar)
        val date: TextView = v.findViewById(R.id.date_text_view)
        val review: TextView = v.findViewById(R.id.review_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if(viewType == Type.HEADER) {
            HeaderViewHolder(layoutInflater.inflate(R.layout.review_header, parent, false))
        } else {
            ReviewViewHolder(layoutInflater.inflate(R.layout.card_review, parent, false))
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is HeaderViewHolder) {
            val noInfo = holder.itemView.context.getString(R.string.info_unavailable)
            holder.address.text = if(info.address != "") info.address else noInfo
            holder.openingHours.text = if(info.openingHours != "")
                info.openingHours.replace("[", "")
                    .replace("]", "")
                    .replace(", ", System.lineSeparator()) else noInfo
            holder.phoneNumber.text = if(info.phoneNumber != "") info.phoneNumber else noInfo
            holder.website.text = if(info.website != "") info.website else noInfo

            // Set up call button
            if(info.phoneNumber != "") {
                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = Uri.parse("tel:" + info.phoneNumber)
                holder.callButton.setOnClickListener {
                    holder.itemView.context.startActivity(callIntent)
                }
            } else {
                holder.callButton.setOnClickListener {
                    Toast.makeText(holder.itemView.context, R.string.call_unavailable, Toast.LENGTH_LONG).show()
                }
            }

            // Set up directions button
            val directionsIntent = Intent(Intent.ACTION_VIEW)
            directionsIntent.data = Uri.parse("google.navigation:q=" + info.address.replace(" ", "+"))
            holder.directionsButton.setOnClickListener {
                holder.itemView.context.startActivity(directionsIntent)
            }

            // Set up website button
            if(info.website != "") {
                val websiteIntent = Intent(Intent.ACTION_VIEW)
                websiteIntent.data = Uri.parse(info.website)
                holder.websiteButton.setOnClickListener {
                    holder.itemView.context.startActivity(websiteIntent)
                }
            } else {
                holder.websiteButton.setOnClickListener {
                    Toast.makeText(holder.itemView.context, R.string.website_unavailable, Toast.LENGTH_LONG).show()
                }
            }

            // Set up review summary
            val overallRating: Float = (info.overallRating * 10).roundToInt() / 10f
            holder.overallRating.text = overallRating.toString()
            holder.overallRatingBar.rating = overallRating
            holder.numberRatings.text = info.numRatings.toString()

            holder.noiseRating.text = when(info.noiseRating.roundToInt()) {
                1 -> holder.itemView.context.getString(R.string.review_option_1)
                2 -> holder.itemView.context.getString(R.string.review_option_2)
                3 -> holder.itemView.context.getString(R.string.review_option_3)
                4 -> holder.itemView.context.getString(R.string.review_option_4)
                5 -> holder.itemView.context.getString(R.string.review_option_5)
                else -> {
                    ""
                }
            } + " noisy"
            val noiseColorId = when(info.noiseRating.roundToInt()) {
                1 -> R.color.color_scale_1
                2 -> R.color.color_scale_2
                3 -> R.color.color_scale_3
                4 -> R.color.color_scale_4
                5 -> R.color.color_scale_5
                else -> {
                    R.color.black
                }
            }
            holder.noiseRating.setTextColor(ContextCompat.getColor(holder.itemView.context, noiseColorId))

            holder.crowdRating.text = when(info.crowdRating.roundToInt()) {
                1 -> holder.itemView.context.getString(R.string.review_option_1)
                2 -> holder.itemView.context.getString(R.string.review_option_2)
                3 -> holder.itemView.context.getString(R.string.review_option_3)
                4 -> holder.itemView.context.getString(R.string.review_option_4)
                5 -> holder.itemView.context.getString(R.string.review_option_5)
                else -> {
                    ""
                }
            } + " crowded"
            val crowdColorId = when(info.crowdRating.roundToInt()) {
                1 -> R.color.color_scale_1
                2 -> R.color.color_scale_2
                3 -> R.color.color_scale_3
                4 -> R.color.color_scale_4
                5 -> R.color.color_scale_5
                else -> {
                    R.color.black
                }
            }
            holder.crowdRating.setTextColor(ContextCompat.getColor(holder.itemView.context, crowdColorId))
        } else if(holder is ReviewViewHolder) {
            firestore.collection("users")
                .document(reviews[position - 1].userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val firstName = documentSnapshot.getString("firstName")
                    val lastName = documentSnapshot.getString("lastName")
                    holder.name.text = "$firstName $lastName"
                }
            val simpleDateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
            simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT")
            holder.date.text = simpleDateFormat.format(reviews[position - 1].date)
            holder.ratingBar.rating = reviews[position - 1].overallRating.toFloat()
            holder.review.text = reviews[position - 1].review
            // Hide review text view if there is no written review
            if(holder.review.text == "") {
                holder.review.visibility = View.GONE
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0) {
            Type.HEADER
        } else {
            Type.REVIEW
        }
    }

    override fun getItemCount(): Int {
        return reviews.size + 1
    }
}