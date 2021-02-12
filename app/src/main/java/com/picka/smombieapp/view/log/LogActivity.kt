package com.picka.smombieapp.view.log

import android.database.Cursor
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.picka.smombieapp.base.BaseActivity
import com.picka.smombieapp.databinding.ActivityLogBinding
import com.picka.smombieapp.utils.LogUtil
import org.json.JSONArray
import org.json.JSONObject


class LogActivity : BaseActivity() {
    private lateinit var binding: ActivityLogBinding
    private var cursor: Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Thread.setDefaultUncaughtExceptionHandler(UncaughtException())

        initView()
    }

    override fun onResume() {
        super.onResume()

        cursor = getLogCursor()
        setLogView()
    }

    private fun initView() {
        binding.logImgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setLogView() {

        val listItem = JSONArray()
        while (cursor?.moveToNext() == true) {
            val item = JSONObject()
            item.put("start", cursor?.getString(2))
            item.put("end", cursor?.getString(3))
            item.put("time", cursor?.getString(4))
            item.put("step", cursor?.getString(5))
            listItem.put(item)
        }

        binding.logRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.logRecyclerview.adapter = LogAdapter(listItem)
    }


    inner class UncaughtException : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            LogUtil.e(t, e)
        }

    }
}