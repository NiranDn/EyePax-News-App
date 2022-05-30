package com.eyepax_news_app.base

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.Window
import androidx.fragment.app.Fragment
import com.eyepax_news_app.R
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

    /**
     * This function used to show waiting spinner
     *
     * @param context activity or fragment context
     */
    fun showLoading(context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.loader)
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }
}