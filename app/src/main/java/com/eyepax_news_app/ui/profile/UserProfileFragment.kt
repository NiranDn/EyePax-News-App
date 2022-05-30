package com.eyepax_news_app.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.eyepax_news_app.base.BaseFragment
import com.eyepax_news_app.databinding.FragmentUserProfileBinding
import com.eyepax_news_app.model.User
import com.eyepax_news_app.ui.AuthActivity
import com.eyepax_news_app.ui.DashBoardActivity
import com.eyepax_news_app.ui.auth.AuthViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_user_profile.*

/**
 * User profile fragment.
 */
class UserProfileFragment : BaseFragment() {
    private lateinit var binding: FragmentUserProfileBinding
    private val mViewModel by lazy { ViewModelProvider(requireActivity()).get(AuthViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initClickListener()
        observeViewModel(view)
    }

    private fun initView() {
        mViewModel.getUser()
    }

    private fun initClickListener() {
        logoutBtn.setOnClickListener {
            mViewModel.userLogout()
            startActivity(Intent(activity, AuthActivity::class.java))
            (activity as DashBoardActivity).finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel(view: View) {
        mViewModel.userLoginDetails.observe(viewLifecycleOwner) { userDetails ->
            if (userDetails?.isNotEmpty() == true) {
                try {
                    val gson = Gson()
                    val userDetails: User = gson.fromJson(userDetails, User::class.java)
                    nameTv.text = "${"Name: "}${userDetails.name}"
                    userNameTv.text = "${"User name: "}${userDetails.userName}"
                    emailTv.text = "${"Email: "}${userDetails.email}"
                } catch (e: Exception) {
                    showAlert("Oops, Something when wrong please try again", view)
                }
            } else {
                showAlert("Oops, Something when wrong please try again", view)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as DashBoardActivity).showAndHideNavigationView(false)
    }

    override fun onResume() {
        super.onResume()
        (activity as DashBoardActivity).showAndHideNavigationView(true)
    }
}