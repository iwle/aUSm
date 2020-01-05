package com.github.iwle.ausm.adapter

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.location.Location
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.iwle.ausm.model.Establishment
import com.github.iwle.ausm.R
import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

class EstablishmentAdapter(
    private val establishments: ArrayList<Establishment>,
    private val clickListener: (Establishment) -> Unit
) : RecyclerView.Adapter<EstablishmentAdapter.CardViewHolder>() {
    var currentLocation: LatLng? = null

    class CardViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        val image: ImageView = v.findViewById(R.id.establishment_image)
        val name: TextView = v.findViewById(R.id.establishment_name)
        val address: TextView = v.findViewById(R.id.establishment_address)
        val rating: TextView = v.findViewById(R.id.establishment_rating)
        val distance: TextView = v.findViewById(R.id.establishment_distance)

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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_establishment, parent, false)
        return CardViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        // Bind click listener
        holder.bind(establishments[position], clickListener)
        // Decode Base64 String to Bitmap
        val imageBytes = Base64.decode(establishments[position].imageBase64, Base64.URL_SAFE)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.image.setImageBitmap(decodedImage)

        holder.name.text = establishments[position].name
        holder.address.text = establishments[position].address
        holder.rating.text = ((establishments[position].overallRating * 10).roundToInt() / 10f).toString()

        val distance = calculateDistance(
            currentLocation!!.latitude,
            currentLocation!!.longitude,
            establishments[position].latitude,
            establishments[position].longitude)
        if(distance >= 1000) {
            holder.distance.text = "${(distance / 100 / 10.0)}km"
        } else {
            holder.distance.text = "${distance}m"
        }
    }

    override fun getItemCount(): Int {
        return establishments.size
    }

    private fun calculateDistance(fromLat: Double, fromLng: Double, toLat: Double, toLng: Double): Int {
        val distance = FloatArray(2)
        Location.distanceBetween(fromLat, fromLng, toLat, toLng, distance)
        return distance[0].toInt()
    }
}