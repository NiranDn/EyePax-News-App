package com.eyepax_news_app.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eyepax_news_app.model.ApiResponse
import com.eyepax_news_app.model.Category
import com.eyepax_news_app.repository.NewsRepository
import com.eyepax_news_app.utils.CommonUtils
import com.eyepax_news_app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchNewsViewModel @Inject constructor(
    private val repository: NewsRepository
): ViewModel() {

    private val _latestNews = MutableLiveData<Resource<ApiResponse>>()
    val latestNews: MutableLiveData<Resource<ApiResponse>> get() = _latestNews

    private val _categories = MutableLiveData<MutableList<Category>>()
    val categories: MutableLiveData<MutableList<Category>> get() = _categories

    private val _filteredNews = MutableLiveData<Resource<ApiResponse>>()
    val filteredNews: MutableLiveData<Resource<ApiResponse>> get() = _filteredNews

    private var pageIndex : Int = 1
    private var filterNewsResponse: ApiResponse? = null
    var isClearPreviousData = false

    /**
     * Get latest news to show in top list
     */
    fun getLatestNews(countryCode: String) {
        viewModelScope.launch {
            _latestNews.postValue(Resource.Loading())
            val response = repository.getLatestNews(countryCode = countryCode)
            _latestNews.postValue(getNewsForNewPageByCategory(response))
        }
    }

    /**
     * Get categories
     */
    fun getCategories() {
        _categories.postValue(repository.getCategories())
    }

    /**
     * Get filtered news by category
     */
    fun filterNews(searchKey: String, sortBy: String = "popularity") {
        viewModelScope.launch {
            _filteredNews.postValue(Resource.Loading())
            val response = repository.getFilteredNews(searchKey = searchKey, sortBy = sortBy, pageIndex = pageIndex)
            _filteredNews.postValue(getNewsForNewPageByCategory(response))
        }
    }

    private fun getNewsForNewPageByCategory(response: Response<ApiResponse>): Resource<ApiResponse> {
        if (response.isSuccessful) {
            response.body()?.let { filteredResponse ->
                pageIndex++
                if(filterNewsResponse == null) {
                    filterNewsResponse = filteredResponse
                } else {
                    if(isClearPreviousData) {
                        filterNewsResponse?.articles?.clear()
                    }
                    val oldNewsList = filterNewsResponse?.articles
                    val newNewsList = filteredResponse.articles
                    oldNewsList?.addAll(newNewsList)
                }
                return Resource.Success(filterNewsResponse ?: filteredResponse)
            }
        } else {
            return Resource.Error(
                CommonUtils.getErrorMessage(response.errorBody().toString())
            )
        }
        return Resource.Error(response.errorBody().toString())
    }
}