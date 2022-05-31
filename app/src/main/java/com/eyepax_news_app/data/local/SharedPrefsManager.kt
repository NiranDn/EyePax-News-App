package com.eyepax_news_app.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.eyepax_news_app.R
import javax.inject.Inject

class SharedPrefsManager @Inject constructor(
    private val mContext: Context
) {
    private val generatePrefPackageIdentifier = mContext.resources.getString(R.string.generatePrefPackageIdentifier)
    var sharedPrefs: SharedPreferences = mContext.getSharedPreferences(generatePrefPackageIdentifier, Context.MODE_PRIVATE)

    companion object {
        const val KEY_USER_LOGIN_DETAILS = "userLoginDetails"
    }

    var saveUserLoginDetails: String
        get() = sharedPrefs[KEY_USER_LOGIN_DETAILS] ?: ""
        set(value) {
            sharedPrefs[KEY_USER_LOGIN_DETAILS] = value
        }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    private operator fun SharedPreferences.set(key: String, value: Any?) {
        when (value) {
            is String -> edit { it.putString(key, value)}
            else -> Log.e(SharedPrefsManager::javaClass.name, "Unsupported property type")
        }
    }

    private inline operator fun <reified T: Any> SharedPreferences.get(
        key: String,
        defaultValue: T? = null
    ): T? {
        return when (T::class) {
            String::class -> getString(key, defaultValue as? String) as T?
            else -> {
                Log.e(SharedPrefsManager::javaClass.name, "Unsupported property type"); null
            }
        }
    }

    fun userLogout() {
        saveUserLoginDetails = ""
    }

}