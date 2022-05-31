package com.eyepax_news_app.model

data class ApiResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int,
    val message: String
)