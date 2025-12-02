package com.edu.dam.di

import android.content.Context
import androidx.room.Room
import com.edu.dam.data.local.BooksDatabase
import com.edu.dam.data.local.dao.BooksDao
import com.edu.dam.data.repository.BookRepositoryImpl
import com.edu.dam.data.repository.BooksRepository

object ServiceLocator {
    @Volatile
    private var database: BooksDatabase? = null

    @Volatile
    private var booksRepository: BooksRepository? = null

    fun provideBooksDatabase(context: Context): BooksDatabase{
        return database ?: synchronized(this){
            database ?: Room.databaseBuilder(
                context.applicationContext,
                BooksDatabase::class.java,
                "books.db"
            ).build().also { database = it }
        }
    }

    fun provideBooksDao(context: Context): BooksDao =
        provideBooksDatabase(context).booksDao()

    fun provideBooksRepository(context: Context): BooksRepository {
        return booksRepository ?: synchronized(this) {
            booksRepository ?: BookRepositoryImpl(provideBooksDao(context)).also {
                booksRepository = it
            }
        }
    }
}