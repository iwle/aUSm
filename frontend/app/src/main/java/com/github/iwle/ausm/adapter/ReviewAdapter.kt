package com.github.iwle.ausm.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.iwle.ausm.R
import com.github.iwle.ausm.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import me.zhanghai.android.materialratingbar.MaterialRatingBar

class ReviewAdapter (
    private val reviews: ArrayList<Review>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    object Type {
        const val HEADER = 0
        const val REVIEW = 1
    }

    class HeaderViewHolder(v: View) : RecyclerView.ViewHolder(v) {

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

        } else if(holder is ReviewViewHolder) {
            firestore.collection("users")
                .document(reviews[position - 1].userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val firstName = documentSnapshot.getString("firstName")
                    val lastName = documentSnapshot.getString("lastName")
                    holder.name.text = "$firstName $lastName"
                }
            holder.ratingBar.rating = reviews[position - 1].overallRating.toFloat()
            holder.review.text = reviews[position - 1].review
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