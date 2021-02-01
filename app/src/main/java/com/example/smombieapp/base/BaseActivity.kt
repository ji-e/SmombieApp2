package com.example.smombieapp.base

import android.app.Activity
import android.app.ActivityManager
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity:AppCompatActivity() {
    /**
     * 앱 종료
     */
    fun exitApp() {
        finishAffinity()
        Handler(Looper.getMainLooper()).postDelayed({
            android.os.Process.killProcess(android.os.Process.myPid())
        }, 200)
    }
}