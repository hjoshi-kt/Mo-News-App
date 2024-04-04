package com.example.newsapp.models

// This is the Pojo class for news response

data class NewsApiResponse(val status : String?, val articles : MutableList<Articles>?)

data class Articles(val source : Source?, val author : String?, val title : String?, val description : String?, val url : String?, val urlToImage : String?, val publishedAt : String?, val content : String?)

data class Source(val id : String?, val name : String?)

