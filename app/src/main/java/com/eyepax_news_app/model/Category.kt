package com.eyepax_news_app.model

data class Category(
    val categoryId: Int,
    val categoryName: String,
    var isSelected: Boolean = false
)
