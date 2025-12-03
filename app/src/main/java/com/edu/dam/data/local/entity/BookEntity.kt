package com.edu.dam.data.local.entity
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "books",
    indices = [Index("updatedAt"), Index("isFavorite")]
)
data class BookEntity (
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val numPage: Int,
    val synopsis: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isFavorite: Boolean
)
