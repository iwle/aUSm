package com.github.iwle.ausm.model

data class User(
    var firstName: String = "",
    var lastName: String = "",
    var reviewsList: ArrayList<String> = ArrayList()
)