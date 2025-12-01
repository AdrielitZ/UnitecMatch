package com.example.unitecmatch.data.models

import androidx.annotation.DrawableRes

data class User(
    val id: Int,
    val name: String,
    val lastName: String,
    val age: Int,
    val interests: List<String>,
    @DrawableRes val photoResId: Int,
    val description: String
)
