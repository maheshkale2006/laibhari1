package com.example.laihari.models

data class User(
    val name: String? = "",
    val email: String? = "",
    val phone: String? = "",
    val userType: String? = "Customer" // Add user type field
)