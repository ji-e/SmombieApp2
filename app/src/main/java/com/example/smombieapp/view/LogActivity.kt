package com.example.smombieapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.smombieapp.databinding.ActivityLogBinding
import com.example.smombieapp.databinding.ActivityMainBinding
import com.example.smombieapp.utils.DBHelper
import com.example.smombieapp.utils.LogUtil

class LogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getLogList()
    }

    private fun getLogList() {
        var cursor = DBHelper(this).selectAll()

        val sb = StringBuffer()
        while (cursor.moveToNext()) {
            LogUtil.d(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5))
            sb.append("${cursor.getInt(0)}," +
                    "${cursor.getString(1)}," +
                    "${cursor.getString(2)}," +
                    "${cursor.getString(3)}," +
                    "${cursor.getString(4)}," +
                    "${cursor.getString(5)}\n")

        }
        binding.logTv.text = sb.toString()
    }
}