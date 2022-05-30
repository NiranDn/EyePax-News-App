package com.eyepax_news_app.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.eyepax_news_app.base.BaseActivity
import com.eyepax_news_app.databinding.ActivitySplashBinding
import com.eyepax_news_app.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(AuthViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeViewModel()
        checkIsUserLoggedIn()
    }

    private fun checkIsUserLoggedIn() {
        mViewModel.getUser()
    }

    private fun observeViewModel() {
        mViewModel.userLoginDetails.observe(this) { userDetails ->
            if (userDetails?.isNotEmpty() == true) {
                launchActivity(DashBoardActivity())
            } else {
                launchActivity(AuthActivity())
            }
        }
    }
}