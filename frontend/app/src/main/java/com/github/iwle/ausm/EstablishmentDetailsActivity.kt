package com.github.iwle.ausm

import android.app.Activity
import android.os.Bundle
import com.github.iwle.ausm.model.Establishment

class EstablishmentDetailsActivity : Activity() {
    private lateinit var establishment: Establishment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_establishment_details)

        establishment = intent.getSerializableExtra("establishment") as Establishment
    }
}