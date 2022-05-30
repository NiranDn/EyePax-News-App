package com.eyepax_news_app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.eyepax_news_app.R
import com.eyepax_news_app.databinding.ActivityDashBoardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_dash_board.*

@AndroidEntryPoint
class DashBoardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navigationView: BottomNavigationView = binding.navigationView
        val navController = findNavController(R.id.dashboardNavFragment)
        navigationView.setupWithNavController(navController)
    }

    fun showAndHideNavigationView(isShow: Boolean) {
        if(isShow) {
            navigationView.visibility = View.VISIBLE
        } else {
            navigationView.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.dashboardNavFragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}