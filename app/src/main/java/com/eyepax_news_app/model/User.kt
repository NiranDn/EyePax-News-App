package com.eyepax_news_app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "users"
)
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var name: String? = "",
    var userName: String? = "",
    var password: String? = "",
    var email: String? = ""
) : Serializable
