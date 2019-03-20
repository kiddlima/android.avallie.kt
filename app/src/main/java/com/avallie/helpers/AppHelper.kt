package com.avallie.helpers

import android.content.Context
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.View
import androidx.core.content.ContextCompat
import com.avallie.R
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

        fun getSnackbar(context: Context, view: View, message: String): Snackbar{
            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)

            snackbar.view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorError))

            snackbar.setActionTextColor(Color.WHITE)

            return snackbar
        }
    }
}