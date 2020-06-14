package com.avallie.helpers

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class FormatterHelper {

    companion object {
        fun dateFromServer(date: String): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            var formattedDateString = ""

            formatter.parse(date).run {
                formattedDateString =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(this)
            }

            return formattedDateString
        }

        fun dateToServer(date: String): String {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            var formattedDateString = ""

            formatter.parse(date).run {
                formattedDateString =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(this)
            }

            return formattedDateString
        }

        fun stringToDate(date: String, pattern: String): Date {
            val formatter = SimpleDateFormat(pattern, Locale.getDefault())

            return formatter.parse(date)
        }

        fun toCurrency(value: Double): String {
            return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(value)
        }
    }

}