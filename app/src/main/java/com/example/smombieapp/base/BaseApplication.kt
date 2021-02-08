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


class BaseApplication :Application(){
    companion object {
        lateinit var instance: BaseApplication
        lateinit var useSharedPreference: UseSharedPreferences  // 프리퍼런스
        val DIR by lazy { instance.getExternalFilesDir(null)?.absolutePath + "/Smombie" }
        val dd by lazy{ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}
        val FILE_PATH_NAME by lazy { DIR + File.separator }

//        Environment.getExternalStorageDirectory().getPath()
    }

    fun getDate():String {
        return  SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    }
    fun getTime():String {
        val dateStart = StepService.startServiceTime
        val dateEnd = StepService.endServiceTime

        val format = SimpleDateFormat("yyyyMMdd HH:mm:ss")

        var d1: Date? = null
        var d2: Date? = null
        try {
            d1 = format.parse(dateStart)
            d2 = format.parse(dateEnd)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        // Get msec from each, and subtract.

        // Get msec from each, and subtract.
        val diff = d2!!.time - d1!!.time
        val diffSeconds = diff / 1000 % 60
        val diffMinutes = diff / (60 * 1000) % 60
        val diffHours = diff / (60 * 60 * 1000)
        LogUtil.d("Time in seconds: $diffSeconds seconds.")
        LogUtil.d("Time in minutes: $diffMinutes minutes.")
        LogUtil.d("Time in hours: $diffHours hours.")

        return  "$diffHours:$diffMinutes:$diffSeconds"
    }

    fun getDateTime():String{
        return  SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.getDefault()).format(Date())
    }



    override fun onCreate() {
        super.onCreate()
        instance = this
        useSharedPreference = UseSharedPreferences(this)
    }
}