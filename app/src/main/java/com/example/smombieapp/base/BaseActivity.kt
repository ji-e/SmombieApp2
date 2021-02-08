package com.example.smombieapp.base

import android.app.Activity
import android.app.ActivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smombieapp.utils.LogUtil

open class BaseActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Thread.setDefaultUncaughtExceptionHandler(UncaughtException())
    }
    /**
     * 앱 종료
     */
    fun exitApp() {
        finishAffinity()
        Handler(Looper.getMainLooper()).postDelayed({
            android.os.Process.killProcess(android.os.Process.myPid())
        }, 200)
    }

    inner class UncaughtException :Thread.UncaughtExceptionHandler{
        override fun uncaughtException(t: Thread, e: Throwable) {
            LogUtil.e(this@BaseActivity,e)
        }

    }
}