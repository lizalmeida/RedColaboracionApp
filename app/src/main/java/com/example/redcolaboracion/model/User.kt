package com.example.redcolaboracion.model

data class User(
    val email: String,
    val name: String,
    val lastname: String,
    val imageUrl: String?,
    val phone: String,
    val address: String,
    val location: String
)