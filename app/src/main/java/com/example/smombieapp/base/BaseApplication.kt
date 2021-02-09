package com.example.smombieapp.base

import android.app.Application
import android.os.Environment
import com.example.smombieapp.service.StepService
import com.example.smombieapp.utils.LogUtil
import com.example.smombieapp.utils.UseSharedPreferences
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
            val format = SimpleDateFormat("yyyyMMdd HH:mm:ss")
            val dateStart = format.parse(StepService.startServiceTime)
            val dateEnd = format.parse(StepService.endServiceTime)

            val diff = dateEnd.time - dateStart.time
            val diffSeconds = abs(diff / 1000 % 60)
            val diffMinutes = abs(diff / (60 * 1000) % 60)
            val diffHours = abs(diff / (60 * 60 * 1000))
            LogUtil.d("$diffHours:$diffMinutes:$diffSeconds")

            return "$diffHours:$diffMinutes:$diffSeconds"
        } catch (e: ParseException) {
            LogUtil.e(e)
            return ""
        }
    }




}