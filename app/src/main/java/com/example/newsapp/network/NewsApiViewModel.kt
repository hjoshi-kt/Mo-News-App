package com.example.newsapp.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.models.NewsApiResponse
import com.example.newsapp.util.Utils.Companion.API_RESPONSE_ERROR_TAG
import com.example.newsapp.util.Utils.Companion.API_URL
import kotlinx.coroutines.launch

/**
 * ViewModel class for making API calls and handling the response.
 *
 * @property repository The repository responsible for fetching data from the News API.
 */

class NewsApiViewModel(private val repository: NewsApiRepository) : ViewModel() {

    // LiveData to hold the API response
    val apiResponse = MutableLiveData<NewsApiResponse?>()

    /**
     * Function to initiate the API call using the provided repository.
     * Handles both success and error cases and updates the [apiResponse] LiveData accordingly.
     */
    fun makeApiCall() {
        viewModelScope.launch {
            try {
                // Make API call using repository
                when (val response = repository.fetchData(API_URL)) {
                    is Result.Success -> {
                        // API call successful, update LiveData with the response data
                        val newsResponse = response.data
                        apiResponse.postValue(newsResponse)
                    }
                    is Result.Error -> {
                        // API call resulted in an error, log the error message and update LiveData to null
                        val errorMessage = response.errorMessage
                        Log.e(API_RESPONSE_ERROR_TAG,errorMessage)
                        apiResponse.postValue(null)
                    }
                }
            } catch (e: Exception) {
                // Exception occurred during the API call, print stack trace and update LiveData to null
                e.printStackTrace()
                apiResponse.postValue(null)
            }
        }
    }
}