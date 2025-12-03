package com.edu.dam.data.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val numPage: Int,
    val synopsis: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isFavorite: Boolean = false
)
