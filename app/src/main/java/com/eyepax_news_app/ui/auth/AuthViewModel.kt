package com.eyepax_news_app.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eyepax_news_app.data.local.SharedPrefsManager
import com.eyepax_news_app.model.User
import com.eyepax_news_app.repository.AuthRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val sharedPrefsHelper: SharedPrefsManager
) : ViewModel() {

    private val _userId = MutableLiveData<Long?>()
    val userId: MutableLiveData<Long?> get() = _userId

    private val _matchedDbUsersList = MutableLiveData<List<User>?>()
    val matchedDbUsersList: MutableLiveData<List<User>?> get() = _matchedDbUsersList

    private val _userLoginDetails = MutableLiveData<String?>()
    val userLoginDetails: MutableLiveData<String?> get() = _userLoginDetails

    private val _userNameMatchedList = MutableLiveData<List<User>?>()
    val userNameMatchedList: MutableLiveData<List<User>?> get() = _userNameMatchedList

    fun signupUser(user: User) {
        viewModelScope.launch {
            _userId.postValue(repository.createUser(user))
        }
    }

    fun saveUser(user: String) {
        viewModelScope.launch {
            sharedPrefsHelper.saveUserLoginDetails = user
        }
    }

    fun getUser() {
        viewModelScope.launch {
            _userLoginDetails.postValue(sharedPrefsHelper.saveUserLoginDetails)
        }
    }

    fun getUserFromDB(username: String, password: String) {
        viewModelScope.launch {
            val users = repository.getUser(username, password).value?.toMutableList()
            _matchedDbUsersList.postValue(users)
        }
    }

    fun userValidation(username: String) {
        viewModelScope.launch {
            val users = repository.getMatchedUser(username).value?.toMutableList()
            _userNameMatchedList.postValue(users)
        }
    }

    fun userLogout() {
        viewModelScope.launch {
            sharedPrefsHelper.userLogout()
        }
    }
}