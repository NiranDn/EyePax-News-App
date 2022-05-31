package com.eyepax_news_app.remote

import com.eyepax_news_app.Globals.API_KEY
import com.eyepax_news_app.Globals.PAGE_SIZE
import com.eyepax_news_app.Globals.SORT_BY
import com.eyepax_news_app.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("v2/top-headlines")
    suspend fun getLatestNews(
        @Query("country") countryCode: String = "us",
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("pageSize") pageSize: Int = PAGE_SIZE,
        @Query("page") pageIndex: Int = 1
    ): Response<ApiResponse>

    @GET("v2/top-headlines")
    suspend fun getNewsByCategory(
        @Query("category") category: String,
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("pageSize") pageSize: Int,
        @Query("page") pageIndex: Int = 1
    ): Response<ApiResponse>

    @GET("v2/everything")
    suspend fun getFilteredNews(
        @Query("q") filter: String, @Query("apiKey") apiKey: String = API_KEY,
        @Query("sortBy") sortBy: String = SORT_BY,
        @Query("pageSize") pageSize: Int, @Query("page") pageIndex: Int
    ): Response<ApiResponse>
}
