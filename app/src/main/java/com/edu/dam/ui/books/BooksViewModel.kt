package com.edu.dam.ui.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.edu.dam.data.model.Book
import com.edu.dam.data.repository.BooksRepository
import com.edu.dam.di.ServiceLocator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class BooksViewModel (
    private val repository: BooksRepository
) : ViewModel() {
    val books: StateFlow<List<Book>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun observeBook(id: String): Flow<Book?> = repository.observeById(id)

    suspend fun addBook(title: String, author: String, numPage: Int, synopsis: String?, isFavorite: Boolean): Boolean =
        runCatching { repository.addBook(title, author, numPage,synopsis, isFavorite) }.isSuccess

    suspend fun updateBook(id: String, title: String, numPage: Int, synopsis: String?): Boolean =
        runCatching { repository.updateBook(id, title, numPage, synopsis) }.isSuccess

    suspend fun toggleFavorite(id: String): Boolean =
        runCatching { repository.toggleFavorite(id) }.isSuccess

    suspend fun deleteBook(id: String): Boolean =
        runCatching { repository.deleteBook(id) }.isSuccess
}

class BooksViewModelFactory(
    private val context: android.content.Context
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(BooksViewModel::class.java)){
            val repository = ServiceLocator.provideBooksRepository(context)
            @Suppress("UNCHECKED_CAST")
            return BooksViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknow View Class: ${modelClass.name}")
    }
}
