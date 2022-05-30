package com.eyepax_news_app.ui.dashboard

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
class DashboardViewModel @Inject constructor(
    private val repository: NewsRepository
): ViewModel(){

    private val _latestNews = MutableLiveData<Resource<ApiResponse>>()
    val latestNews: MutableLiveData<Resource<ApiResponse>> get() = _latestNews

    private val _categories = MutableLiveData<MutableList<Category>>()
    val categories: MutableLiveData<MutableList<Category>> get() = _categories

    private val _filteredNews = MutableLiveData<Resource<ApiResponse>>()
    val filteredNews: MutableLiveData<Resource<ApiResponse>> get() = _filteredNews

    var pageIndex : Int = 0
    var filterNewsResponse: ApiResponse? = null
    var isClearPreviousData = false

    /**
     * Get latest news to show in top list
     */
    fun getLatestNews(countryCode: String) {
        viewModelScope.launch {
            _latestNews.postValue(Resource.Loading())
            val response = repository.getLatestNews(countryCode = countryCode)
            when (response.isSuccessful) {
                response.isSuccessful -> {
                    response.body()?.let { latestNews ->
                        _latestNews.postValue( Resource.Success(latestNews))
                    }
                }
                else -> {
                    _latestNews.postValue(Resource.Error(CommonUtils.getErrorMessage(response.errorBody())))
                }
            }
        }
    }

    /**
     * Get categories
     */
    fun getCategories() {
        categories.postValue(repository.getCategories())
    }

    /**
     * Get filtered news by category
     */
    fun filterNewsByCategory(category: String) {
        viewModelScope.launch {
            val response = repository.getNewsByCategory(category = category, pageIndex = pageIndex)
            _filteredNews.postValue(getNewsForNewPageByCategory(response))
        }
    }

    fun getNewsForNextPage(category: String) {
        viewModelScope.launch {
            pageIndex += 1
            filterNewsByCategory(category)
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
                CommonUtils.getErrorMessage(response.errorBody())
            )
        }
        return Resource.Error(response.errorBody().toString())
    }
}