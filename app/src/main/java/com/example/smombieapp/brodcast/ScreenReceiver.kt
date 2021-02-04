package com.example.smombieapp.brodcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.smombieapp.utils.LogUtil


class ScreenReceiver : BroadcastReceiver() {
    var isScreenON = true
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action
        LogUtil.e("isScreenON : $action")

        when (action) {
            Intent.ACTION_SCREEN_ON -> {
                isScreenON = true
            }
            Intent.ACTION_SCREEN_OFF -> {
                isScreenON = false
            }
        }
    }
}

