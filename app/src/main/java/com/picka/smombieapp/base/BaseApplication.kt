package com.picka.smombieapp.base

import android.app.Application
import com.picka.smombieapp.service.StepService
import com.picka.smombieapp.utils.LogUtil
import com.picka.smombieapp.utils.UseSharedPreferences
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


class BaseApplication : Application() {
    companion object {
        lateinit var instance: BaseApplication
        lateinit var useSharedPreference: UseSharedPreferences  // 프리퍼런스
        val DIR by lazy { instance.getExternalFilesDir(null)?.absolutePath + "/Smombie" }
        val FILE_PATH_NAME by lazy { DIR + File.separator }

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        useSharedPreference = UseSharedPreferences(this)
    }

    /** 현재 날짜 구하기 */
    fun getDate(): String {
        return SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    }

    /** 현재 날짜 및 시간구하기 */
    fun getDateTime(): String {
        return SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.getDefault()).format(Date())
    }

    /** 실행 시간 구하기 */
    fun getTime(): String {
        try {
            if (StepService.startServiceDate.isEmpty() || StepService.endServiceTime.isEmpty()) {
                return ""
            }
            val format = SimpleDateFormat("yyyyMMdd HH:mm:ss")
            val dateStart = format.parse(StepService.startServiceTime)
            val dateEnd = format.parse(StepService.endServiceTime)

            val diff = dateEnd.time - dateStart.time
            val diffSeconds = String.format("%2d", abs(diff / 1000 % 60))
            val diffMinutes = String.format("%2d", abs(diff / (60 * 1000) % 60))
            val diffHours = abs(diff / (60 * 60 * 1000))

            return "$diffHours:$diffMinutes:$diffSeconds"
        } catch (e: ParseException) {
            LogUtil.e(e)
            return ""
        }
    }

    /** 총 실행 시간 구하기 */
    fun getTotalTime(t1: String, t2: String): String {
        try {
            if (t1.isEmpty()) {
                return t2
            } else if (t2.isEmpty()) {
                return t1
            } else if (t1.isEmpty() && t2.isEmpty()) {
                return ""
            }
            val d1 = (t1.replace(" ", "")).split(":")
            val d2 = (t2.replace(" ", "")).split(":")

            var diffSeconds = 0
            var diffMinutes = 0
            var diffHours = 0

            diffSeconds = (d1[2].toInt() + d2[2].toInt()) % 60
            diffMinutes = (d1[2].toInt() + d2[2].toInt()) / 60
            diffMinutes += (d1[1].toInt() + d2[1].toInt()) % 60
            diffHours = (d1[1].toInt() + d2[1].toInt()) / 60
            diffHours += d1[0].toInt() + d2[0].toInt()

            return "$diffHours:$diffMinutes:$diffSeconds"
        } catch (e: ParseException) {
            LogUtil.e(e)
            return ""
        }

    }


}