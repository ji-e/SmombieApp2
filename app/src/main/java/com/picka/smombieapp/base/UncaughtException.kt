package com.picka.smombieapp.base

import com.picka.smombieapp.utils.LogUtil

class UncaughtException: Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        LogUtil.e(t,e)
    }
}