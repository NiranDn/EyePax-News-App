package com.eyepax_news_app.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import java.lang.Exception

object CommonUtils {
    fun getErrorMessage(response: String?): String {
        return try {
            val jsonObject: JsonObject = Gson().fromJson(response, JsonObject::class.java)
            jsonObject["message"].toString()
        } catch (e: Exception) {
            "Server error"
        }
    }
}