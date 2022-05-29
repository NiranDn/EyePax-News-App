package com.eyepax_news_app.base

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment(): Fragment() {

    /**
     * Show message
     *
     * @param message Alert message
     */
    fun showAlert(message: String, view: View) {
        val snackBar = Snackbar
            .make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Ok") { }
        snackBar.show()
    }
}