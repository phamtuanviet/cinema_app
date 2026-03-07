package com.example.myapplication.domain.model

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val phone : String,
    val isVerified : Boolean,
    val role : String
)