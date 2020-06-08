package com.avallie.helpers

import java.text.SimpleDateFormat
import java.util.*

class FormatterHelper {

    companion object {
        fun dateFromServer(date: String): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            var formattedDateString = ""

            formatter.parse(date).run {
                formattedDateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(this)
            }

            return formattedDateString

        }
    }

}