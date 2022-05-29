package com.eyepax_news_app.repository

import com.eyepax_news_app.db.AppDatabase
import com.eyepax_news_app.model.User
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val db: AppDatabase
) {

    suspend fun createUser(user: User) =
        db.userDao().saveUser(user)

    fun getUser(username: String, password: String) =
        db.userDao().getUser(username, password)

    fun getMatchedUser(username: String) =
        db.userDao().getMatchedUser(username)
}