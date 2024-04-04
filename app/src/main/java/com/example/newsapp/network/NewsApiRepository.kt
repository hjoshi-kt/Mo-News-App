package com.example.newsapp.network

import com.example.newsapp.models.NewsApiResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Repository class for interacting with the News API and fetching data.
 */

class NewsApiRepository {

    /**
     * Suspended function to fetch data from the News API.
     *
     * @param apiUrl The URL of the News API.
     * @return Result<NewsApiResponse> representing the success or failure of the data retrieval.
     */

    suspend fun fetchData(apiUrl: String): Result<NewsApiResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Create URL and open connection
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection

                // Set request method to GET
                connection.requestMethod = "GET"
                val responseCode = connection.responseCode

                // Check if the response code is HTTP_OK (200)
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read response from input stream
                    val reader = BufferedReader(InputStreamReader(connection.inputStream, "UTF-8"))
                    val response = StringBuilder()
                    var line: String?

                    // Read each line of the response
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line).append("\n")
                    }

                    reader.close()

                    // Parse JSON response using Gson
                    val gson = Gson()
                    val newsResponse =
                        gson.fromJson(response.toString(), NewsApiResponse::class.java)

                    // Return error result with HTTP error message
                    Result.Success(newsResponse)
                } else {
                    // Return error result with network error message
                    Result.Error("HTTP Error: $responseCode")
                }
            } catch (e: Exception) {
                Result.Error("Network error: ${e.message}")
            }
        }
    }
}


/**
 * Sealed class representing the result of a data operation.
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val errorMessage: String) : Result<Nothing>()
}
