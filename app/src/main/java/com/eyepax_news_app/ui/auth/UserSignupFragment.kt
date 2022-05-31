package com.eyepax_news_app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.eyepax_news_app.Globals
import com.eyepax_news_app.base.BaseFragment
import com.eyepax_news_app.databinding.FragmentUserSignupBinding
import com.eyepax_news_app.model.User
import com.eyepax_news_app.ui.AuthActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_user_signup.*

/**
 * User registration fragment
 */
class UserSignupFragment: BaseFragment() {
    private lateinit var binding: FragmentUserSignupBinding
    private val mViewModel by lazy { ViewModelProvider(requireActivity()).get(AuthViewModel::class.java) }
    lateinit var mUser: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUserSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners(view)
        observeViewModel(view)
    }

    private fun initClickListeners(view: View) {
        registerBtn.setOnClickListener { signupUser(view) }
    }

    private fun observeViewModel(view: View) {
        mViewModel.userId.observe(viewLifecycleOwner) { userId ->
            if (userId != 0L) {
                mUser.id = userId?.toInt()
                val gson = Gson()
                val userDetails = gson.toJson(mUser)
                mViewModel.saveUser(userDetails)
                (activity as AuthActivity).launchDashboardActivity()
            } else {
                showAlert("Oops, Something when wrong", view)
            }
        }

        mViewModel.userNameMatchedList.observe(viewLifecycleOwner) { users ->
            if (users == null) {
                // User signup
                mViewModel.signupUser(mUser)
            } else {
                showAlert("User name already taken", view)
            }
        }


    }

    /**
     * Get user details
     */
    private fun signupUser(view: View) {
        val name = binding.nameEt.text.trim().toString()
        val userName = binding.userNameEt.text.trim().toString()
        val email = binding.emailEt.text.trim().toString()
        val password = binding.passwordEt.text.trim().toString()
        val confirmationPassword = binding.reenterPasswordEt.text.trim().toString()

        // Validating user details and signup
        if (isDataValid(name, userName, email, password, confirmationPassword, view) ) {
            mUser = User(name = name, userName = userName, email = email, password = password)
            mViewModel.userValidation(userName)
        }
    }

    /**
     * Validating user details
     */
    private fun isDataValid( name: String, userName: String, email: String, password: String, confirmationPassword: String, view: View): Boolean {
        return when {
            name.isEmpty() && userName.isEmpty() && email.isEmpty() && password.isEmpty() && confirmationPassword.isEmpty() -> {
                showAlert("Please enter all the details", view)
                false
            }
            !email.matches(Globals.EMAIL_PATTERN.toRegex()) -> {
                showAlert("Please enter valid email address", view)
                false
            }
            !password.equals(confirmationPassword, ignoreCase = false) -> {
                showAlert("Password not matched", view)
                false
            }
            else -> true
        }
    }
}