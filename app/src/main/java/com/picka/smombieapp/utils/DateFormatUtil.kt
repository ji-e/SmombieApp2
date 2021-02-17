package com.picka.smombieapp.utils

import com.picka.smombieapp.base.BaseApplication
import java.text.SimpleDateFormat
import java.util.*

class DateFormatUtil {

    fun getDotDate(d: String): String {
        if (d.length < 8) return ""

        val date = d.replace(" ", "")
        return (Constants.YYYYMMDD_PATTERN).toRegex().replace(date, "$1.$2.$3")
    }

    fun getAMPMTime(t: String): String {
        var time = (t.replace(" ", "")).toInt()
        if (time >= 12) {
            if (time > 12) {
                time -= 12
            }
            return "오후 $time"
        }
        return "오전 $time"
    }

    fun getDateMD(d: String): String {
        val date = d.replace(" ", "")
        if (date.length < 8) return ""

        val today = BaseApplication().getDate()
        val dDate = Date(Date().time + (1000 * 60 * 60 * 24 * -1))
        val dSdf = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
        val yesterday = dSdf.format(dDate)

        return when (date) {
            today -> "오늘"
            yesterday -> "어제"
            else -> "${date.substring(4, 6)}월 ${date.substring(6, 8)}일"
        }
    }

    fun getTimeHMS(t: String): String {
        val time = t.replace(" ", "")
        val timeArr = time.split(":")

        return when {
            timeArr.size < 2 -> "-"
            timeArr.size == 2 -> "${timeArr[0]}분 ${timeArr[1]}초"
            else -> "${timeArr[0]}시간 ${timeArr[1]}분 ${timeArr[2]}초"
        }
    }

    fun getTimeColon(t: String): String {
        if (t.length < 17) return "-"

        val dtArr = t.split(" ")

        if (dtArr.size < 2) return "-"

        return dtArr[1].substring(0, 5)
    }
}