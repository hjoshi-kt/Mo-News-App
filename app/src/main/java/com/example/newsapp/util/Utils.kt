package com.example.newsapp.util

import android.text.TextUtils
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

class Utils {

    companion object {
        const val API_URL = "https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json"
        const val API_RESPONSE_ERROR_TAG = "error occurred"
        private const val REGEX_TO_REMOVE = "\\[\\+\\d+ chars\\]$"
        private const val INPUT_DATE_AND_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        private const val OUTPUT_DATE_AND_TIME_FORMAT = "dd-MMM-yyyy\nhh:mm:ss a"
        const val NOT_AVAILABLE = "Time Not Available"
        val regex = Regex(REGEX_TO_REMOVE)
        const val SORT_BY = "sort_by"
        const val NOTIFICATION_REQUEST_CODE = 0
        const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL"
        const val NOTIFICATION_CHANNEL_NAME = "NEWS_NOTIFICATION"
        const val MOENGAGE_APP_ID = "48VL1XKY8O8FKT6MO00XGEWA_DEBUG"
        const val MO_APP_VERSION_PREF_KEY = "APP_VERSION"
        const val DEFAULT_SAVED_APP_VERSION = -1

        /**
         * Formats the given time string into a human-readable format.
         *
         * @param time The time string to be formatted.
         * @return A formatted time string or "Time Not Available" if parsing fails.
         */
        fun getFormattedDateAndTime(time : String) : String {
            val inputDateAndTimeFormat = SimpleDateFormat(INPUT_DATE_AND_TIME_FORMAT, Locale.getDefault())
            val outputDateAndTimeFormat = SimpleDateFormat(OUTPUT_DATE_AND_TIME_FORMAT, Locale.getDefault())
            try {
                val date = inputDateAndTimeFormat.parse(time) ?: return NOT_AVAILABLE
                return outputDateAndTimeFormat.format(date)
            } catch (e : Exception){
                e.printStackTrace()
                return NOT_AVAILABLE
            }
        }

        /**
         * Formats the given time string into a human-readable format.
         *
         * @param dateAndTime Time which we are receiving from the api as INPUT_DATE_AND_TIME_FORMAT
         * @return Milliseconds in in IST
         */

        fun getMillisecondsFromDateAndTime(dateAndTime : String?) : Long {
            if (TextUtils.isEmpty(dateAndTime)){
                return 0
            }
            val dateFormat = SimpleDateFormat(INPUT_DATE_AND_TIME_FORMAT, Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
            try {
                val date = dateFormat.parse(dateAndTime!!)
                return date?.time ?: 0
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return 0
        }

        /**
         * Filters unwanted characters from the provided news content using a regular expression.
         *
         * @param news The news content to be filtered.
         * @return The filtered news content.
         */
        fun filterNews(news: String): String {
            return news.replace(regex, "")
        }

    }



}