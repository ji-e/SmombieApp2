package com.example.smombieapp.base

import com.example.smombieapp.utils.LogUtil

class UncaughtException: Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        LogUtil.e(t,e)
    }
}