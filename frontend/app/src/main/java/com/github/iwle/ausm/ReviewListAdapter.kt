package com.github.iwle.ausm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ReviewListAdapter(private val establishments: ArrayList<Establishment>) :
        RecyclerView.Adapter<ReviewListAdapter.CardViewHolder>() {
    class CardViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val image: ImageView = v.findViewById<ImageView>(R.id.establishment_image)
        val name: TextView = v.findViewById<TextView>(R.id.establishment_name)
        val location: TextView = v.findViewById<TextView>(R.id.establishment_location)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_card_review, parent, false)
        return CardViewHolder(v)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        Picasso.get().load(establishments[position].imageUrl).into(holder.image)
        holder.name.text = establishments[position].name
        holder.location.text = establishments[position].location
    }

    override fun getItemCount(): Int {
        return establishments.size
    }
}