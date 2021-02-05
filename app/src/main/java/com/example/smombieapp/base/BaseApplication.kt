package com.example.smombieapp.base

import android.app.Application
import com.example.smombieapp.utils.UseSharedPreferences
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class BaseApplication :Application(){
    companion object {
        lateinit var instance: BaseApplication
        lateinit var useSharedPreference: UseSharedPreferences  // 프리퍼런스
        val DIR by lazy { instance.getExternalFilesDir(null)?.absolutePath + "/Smombie" }
        val FILE_PATH_NAME by lazy { DIR + File.separator }
    }


    fun getDateTime():String {
      return  SimpleDateFormat("yyyyMMdd_HH:mm:ss", Locale.getDefault()).format(Date())
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        useSharedPreference = UseSharedPreferences(this)
    }
}