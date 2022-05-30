package com.eyepax_news_app.repository

import com.eyepax_news_app.Globals.PAGE_SIZE
import com.eyepax_news_app.model.Category
import com.eyepax_news_app.remote.ApiService
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsApi: ApiService
) {
    suspend fun getLatestNews(countryCode: String, page: Int = 1) =
        newsApi.getLatestNews(countryCode= countryCode, pageIndex = page)

    fun getCategories(): MutableList<Category> {
        val categories: MutableList<Category> = mutableListOf()
        categories.add(Category(1, "business", true))
        categories.add(Category(2, "entertainment"))
        categories.add(Category(3, "general"))
        categories.add(Category(4, "health"))
        categories.add(Category(5, "science"))
        categories.add(Category(6, "sports"))
        categories.add(Category(7, "technology"))
        return categories
    }

    suspend fun getNewsByCategory(category: String, pageIndex: Int = 1) =
        newsApi.getNewsByCategory(category = category, pageSize = PAGE_SIZE, pageIndex = pageIndex)
}