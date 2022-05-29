package com.eyepax_news_app.ui

import android.os.Bundle
import com.eyepax_news_app.R
import com.eyepax_news_app.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }

    fun launchDashboardActivity() {
        launchActivity(DashBoardActivity())
    }
}