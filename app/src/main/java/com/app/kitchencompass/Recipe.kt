package com.app.kitchencompass

data class Recipe(
    val id: Int,
    val name: String,
    val ingredients: String,
    val instructions: String,
    val estimated_time: String,
    val previewImage: String
)
