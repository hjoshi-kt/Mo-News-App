package com.example.newsapp.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory class for creating instances of [NewsApiViewModel].
 *
 * @property repository The repository used by the ViewModel to interact with the News API.
 */

class NewsApiViewModelFactory(private val repository: NewsApiRepository) : ViewModelProvider.Factory {

    /**
     * Creates an instance of the specified ViewModel class with the provided repository.
     *
     * @param modelClass The class of the ViewModel to be created.
     * @return An instance of [NewsApiViewModel] with the provided repository.
     * @throws IllegalArgumentException if the provided ViewModel class is unknown.
     */

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsApiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsApiViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}