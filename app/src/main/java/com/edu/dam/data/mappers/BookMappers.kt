package com.edu.dam.data.mappers

import com.edu.dam.data.local.entity.BookEntity
import com.edu.dam.data.model.Book

fun BookEntity.toDomain(): Book = Book(
    id = id,
    title = title,
    author = author,
    numPage = numPage,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isFavorite = isFavorite
)

fun Book.toEntity(): BookEntity = BookEntity(
    id = id,
    title = title,
    author = author,
    numPage = numPage,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isFavorite = isFavorite
)