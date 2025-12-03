package com.edu.dam.data.repository

import com.edu.dam.data.local.dao.BooksDao
import com.edu.dam.data.mappers.toDomain
import com.edu.dam.data.mappers.toEntity
import com.edu.dam.data.model.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.UUID
interface BooksRepository {
    fun observeAll(): Flow<List<Book>>
    fun observeFavorites(): Flow<List<Book>>
    fun observeById(id: String): Flow<Book?>

    suspend fun addBook(title: String, author: String, numPage: Int, synopsis: String?, isFavorite: Boolean)
    suspend fun updateBook(id: String, title: String, numPage: Int, synopsis: String? )
    suspend fun toggleFavorite(id: String)
    suspend fun deleteBook(id: String)
}

class BookRepositoryImpl(
    private val dao: BooksDao
): BooksRepository{
    override fun observeAll(): Flow<List<Book>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun observeFavorites(): Flow<List<Book>> =
        dao.getFavorites().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: String): Flow<Book?> =
        dao.getById(id).map { entity -> entity?.toDomain() }


    override suspend fun addBook(
        title: String,
        author: String,
        numPage: Int,
        synopsis: String?,
        isFavorite: Boolean
    ) {
        val now = System.currentTimeMillis()
        val entity = Book(
            id = UUID.randomUUID().toString(),
            title = title,
            author = author,
            numPage = numPage,
            synopsis = synopsis.orEmpty(),
            createdAt = now,
            updatedAt = now,
            isFavorite = isFavorite
        ).toEntity()
        dao.insert(entity)
    }

    override suspend fun updateBook(id: String, title: String, numPage: Int, synopsis: String?) {
        val existing = dao.getById(id).firstOrNull() ?: return
        val updated = existing.copy(
            title = title,
            numPage = numPage,
            synopsis = synopsis,
            updatedAt = System.currentTimeMillis()
        )
        dao.update(updated)
    }

    override suspend fun toggleFavorite(id: String) {
        dao.toggleFavorite(id)
    }

    override suspend fun deleteBook(id: String) {
        dao.deleteById(id)
    }
}