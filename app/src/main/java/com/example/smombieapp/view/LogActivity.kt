package com.example.smombieapp.view

import android.database.Cursor
import android.os.Bundle
import com.example.smombieapp.base.BaseActivity
import com.example.smombieapp.databinding.ActivityLogBinding
import com.example.smombieapp.utils.LogUtil

class LogActivity : BaseActivity() {
    private lateinit var binding: ActivityLogBinding
    private var cursor: Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Thread.setDefaultUncaughtExceptionHandler(UncaughtException())

    }

    override fun onResume() {
        super.onResume()

        cursor = getLogCursor()
        setLogView()
    }

    private fun setLogView() {
        val sb = StringBuffer()
        while (cursor?.moveToNext() == true) {
            sb.append(
//                    "${cursor.getInt(0)}\n" +
//                    "${cursor.getString(1)} // " +
                    "${cursor?.getString(2)} // " +
                            "${cursor?.getString(3)} // " +
                            "${cursor?.getString(4)} // " +
                            "${cursor?.getString(5)}\n\n")

        }

        binding.logTv.text = sb.toString()
    }


    inner class UncaughtException : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            LogUtil.e(t, e)
        }

    }
}