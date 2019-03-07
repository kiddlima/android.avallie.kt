package com.avallie.helpers

import android.content.Context
import android.util.DisplayMetrics
import com.avallie.model.ConstructionPhase

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
    }
}