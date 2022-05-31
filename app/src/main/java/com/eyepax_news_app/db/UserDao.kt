package com.eyepax_news_app.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.eyepax_news_app.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: User): Long

    @Query("SELECT * FROM users WHERE userName = :userName AND password = :password")
    fun getUser(userName: String, password: String): LiveData<List<User>>

    @Query("SELECT * FROM users WHERE userName = :userName")
    fun getMatchedUser(userName: String): LiveData<List<User>>
}