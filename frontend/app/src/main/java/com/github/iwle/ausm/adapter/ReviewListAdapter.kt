package com.github.iwle.ausm.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.iwle.ausm.model.Establishment
import com.github.iwle.ausm.R

class ReviewListAdapter(
    private val establishments: ArrayList<Establishment>,
    private val clickListener: (Establishment) -> Unit
) : RecyclerView.Adapter<ReviewListAdapter.CardViewHolder>() {
    class CardViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        val image: ImageView = v.findViewById(R.id.establishment_image)
        val name: TextView = v.findViewById(R.id.establishment_name)
        val address: TextView = v.findViewById(R.id.establishment_address)
        val rating: TextView = v.findViewById(R.id.establishment_rating)

        fun bind(establishment: Establishment, clickListener: (Establishment) -> Unit) {
            v.setOnClickListener {
                clickListener(establishment)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_card_review, parent, false)
        return CardViewHolder(v)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        // Bind click listener
        holder.bind(establishments[position], clickListener)
        // Decode Base64 String to Bitmap
        val imageBytes = Base64.decode(establishments[position].imageBase64, Base64.URL_SAFE)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.image.setImageBitmap(decodedImage)

        holder.name.text = establishments[position].name
        holder.address.text = establishments[position].address
        holder.rating.text = establishments[position].overallRating.toString()
    }

    override fun getItemCount(): Int {
        return establishments.size
    }
}