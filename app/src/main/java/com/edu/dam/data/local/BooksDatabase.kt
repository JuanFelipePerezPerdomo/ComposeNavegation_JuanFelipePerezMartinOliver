package com.edu.dam.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.edu.dam.data.local.dao.BooksDao
import com.edu.dam.data.local.entity.BookEntity

@Database(
    entities = [BookEntity::class],
    version = 1,
    exportSchema = true
)
abstract class BooksDatabase : RoomDatabase() {
    abstract fun booksDao(): BooksDao
}