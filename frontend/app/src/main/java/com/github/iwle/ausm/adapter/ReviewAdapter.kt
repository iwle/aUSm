package com.github.iwle.ausm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.iwle.ausm.R
import com.github.iwle.ausm.model.Review

class ReviewAdapter (
    private val reviews: ArrayList<Review>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    object Type {
        const val HEADER = 0
        const val REVIEW = 1
    }

    class HeaderViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    }

    class ReviewViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if(viewType == Type.HEADER) {
            HeaderViewHolder(layoutInflater.inflate(R.layout.review_header, parent, false))
        } else {
            ReviewViewHolder(layoutInflater.inflate(R.layout.card_review, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is HeaderViewHolder) {

        } else if(holder is ReviewViewHolder) {

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