package com.eyepax_news_app.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import java.lang.Exception

object CommonUtils {
    fun getErrorMessage(responseBody: ResponseBody?): String {
        try {
            val jsonObject: JsonObject = Gson().fromJson(responseBody.toString(), JsonObject::class.java)
            return jsonObject["message"].toString()
        } catch (e: Exception) {
            return "Server error"
        }
    }
}