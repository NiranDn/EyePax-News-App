package com.eyepax_news_app.base

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity: AppCompatActivity() {

    fun launchActivity(clazz: AppCompatActivity) {
        val intent = Intent(this, clazz::class.java)
        startActivity(intent)
        finish()
    }
}