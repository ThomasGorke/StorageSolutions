package com.thomasgorke.storagesolution.utils

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.thomasgorke.storagesolution.R


interface Snacker {
    fun defineView(view: View)

    fun showDefaultError()
    fun showError(message: String)
    fun showError(@StringRes message: Int)
}

class SnackerImpl(
    private val context: Context
) : Snacker {

    private lateinit var view: View

    override fun defineView(view: View) {
        this.view = view
    }

    override fun showDefaultError() {
        showSnackbar(context.getString(R.string.default_error_msg))
    }

    override fun showError(message: String) {
        showSnackbar(message)
    }

    override fun showError(message: Int) {
        showSnackbar(context.getString(message))
    }

    private fun showSnackbar(
        message: String,
        actionText: String? = null,
        action: (() -> Unit)? = null
    ) {
        if (this::view.isInitialized.not()) {
            throw RuntimeException("View for Snacker not set!!!")
        }
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            .apply {
                if (action != null && actionText != null) {
                    setAction(actionText) { action() }
                }
            }
            .show()
    }
}