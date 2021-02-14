package com.picka.smombieapp.utils

class DateFormatUtil {

    fun dotDate(d: String): String {
        if (d.length < 8) return ""
        val date = d.replace(" ", "")
        return (Constants.YYYYMMDD_PATTERN).toRegex().replace(date, "$1.$2.$3")
    }
}