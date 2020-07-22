package com.avallie.helpers

import android.content.Context
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.avallie.model.ConstructionPhase
import com.google.android.material.snackbar.Snackbar

open class AppHelper {

    companion object {
        fun dpToPx(dp: Int, context: Context): Int {
            val displayMetrics = context.resources.displayMetrics
            return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
        }

        fun getPhaseByCategory(selectedCategory: String?, phases: ArrayList<ConstructionPhase>): String? {
            for (phase in phases) {
                for (category in phase.categories) {
                    if (category == selectedCategory) return phase.icon
                }
            }

            return ""
        }

        fun getErrorSnackbar(context: Context, view: View, message: String): Snackbar{
            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)

            snackbar.view.setBackgroundColor(ContextCompat.getColor(context, com.avallie.R.color.colorError))

            snackbar.setActionTextColor(Color.WHITE)

            return snackbar
        }

        fun getSuccessSnackbar(context: Context, view: View, message: String): Snackbar{
            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)

            snackbar.view.setBackgroundColor(ContextCompat.getColor(context, com.avallie.R.color.colorSuccess))

            snackbar.setActionTextColor(Color.WHITE)

            return snackbar
        }

        fun hideKeyboard(context: Context, view: View) {
            val imm =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}