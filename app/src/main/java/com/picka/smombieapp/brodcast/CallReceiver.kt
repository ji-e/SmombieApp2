package com.picka.smombieapp.brodcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import com.picka.smombieapp.utils.LogUtil


class CallReceiver : BroadcastReceiver() {
    private var phonestate: String? = null
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action.equals("android.intent.action.PHONE_STATE")) {
            val telephonyManager =
                context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            val extras = intent.extras
            if (extras != null) {
                val state = extras.getString(TelephonyManager.EXTRA_STATE) // 현재 폰 상태 가져옴
                if (state == phonestate) {
                    return
                } else {
                    phonestate = state
                }
                if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                    val phoneNo =
                        extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                    LogUtil.d("qqq", phoneNo + "currentNumber")
                    LogUtil.d("qqq", "통화벨 울리는중")


//                     telephonyManager.acceptRingingCall();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    telephonyManager.endCall(); 전화 끊기, 거절 함수이다.
//                }
                } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                    LogUtil.d("qqq", "통화중")
                } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                    LogUtil.d("qqq", "통화종료 혹은 통화벨 종료")
                }
                LogUtil.d("qqq", "phone state : $state")
//                LogUtil.d("qqq", "phone currentPhonestate : $currentPhoneState")
            }
        }
    }
}