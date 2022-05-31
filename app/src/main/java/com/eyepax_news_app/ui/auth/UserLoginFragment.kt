package com.eyepax_news_app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.eyepax_news_app.R
import com.eyepax_news_app.base.BaseFragment
import com.eyepax_news_app.databinding.FragmentUserLoginBinding
import com.eyepax_news_app.ui.AuthActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_user_login.*

/**
 * User login fragment
 */
class UserLoginFragment : BaseFragment() {
    private lateinit var binding: FragmentUserLoginBinding
    private val mViewModel by lazy { ViewModelProvider(requireActivity()).get(AuthViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUserLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners(view)
        observeViewModel(view)
        isUserLogged()
    }

    private fun initClickListeners(view: View) {
        loginBtn.setOnClickListener {
            userLogin(view)
        }
        signupBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.userSignupFragment)
        }
    }

    private fun observeViewModel(view: View) {
        mViewModel.matchedDbUsersList.observe(viewLifecycleOwner) { users ->
            if (users?.isNotEmpty() == true) {
                val gson = Gson()
                val userDetails = gson.toJson(users.first())
                mViewModel.saveUser(userDetails)
            } else {
                showAlert("Oop, Please check user name and password", view)
            }
        }

        mViewModel.userLoginDetails.observe(viewLifecycleOwner) { userLoginDetails ->
            if (userLoginDetails?.isNotEmpty() == true) {
                (activity as AuthActivity).launchDashboardActivity()
            }
        }
    }

    private fun userLogin(view: View) {
        if (binding.userNameEt.text.isNotEmpty() || binding.passwordEt.text.isNotEmpty()) {
            mViewModel.getUserFromDB(binding.userNameEt.text.trim().toString(), binding.passwordEt.text.trim().toString())
        } else {
            showAlert("Please enter user name and password", view)
        }
    }

    private fun isUserLogged() {
        mViewModel.getUser()
    }
}