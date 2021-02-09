package com.example.smombieapp.brodcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.smombieapp.base.BaseApplication
import com.example.smombieapp.service.StepService
import com.example.smombieapp.utils.Constants
import com.example.smombieapp.utils.LogUtil

class SmombieReceiver : BroadcastReceiver() {

    override fun onReceive(mContext: Context, intent: Intent) {

        val action = intent.action

        when {
            action.equals(Intent.ACTION_PACKAGE_ADDED) -> {
                // 앱이 설치되었을 때
                LogUtil.e("ACTION_PACKAGE_ADDED")
                smombieServiceStart(mContext)
            }
            action.equals(Intent.ACTION_PACKAGE_REMOVED) -> {
                // 앱이 삭제되었을 때
                LogUtil.e("ACTION_PACKAGE_REMOVED")
            }
            action.equals(Intent.ACTION_PACKAGE_REPLACED) -> {
                // 앱이 업데이트 되었을 때
                LogUtil.e("ACTION_PACKAGE_REPLACED")
                smombieServiceStart(mContext)

            }
            intent.action.equals(Intent.ACTION_BOOT_COMPLETED) -> {
                // 폰이 부팅되었을 때
                LogUtil.e("ACTION_BOOT_COMPLETED")
                smombieServiceStart(mContext)
            }
        }

    }

    private fun smombieServiceStart(mContext: Context) {
        if (BaseApplication.useSharedPreference.getPreferencesData(Constants.IS_STEP_SERVICE) == "Y") {
            val i = Intent(mContext, StepService::class.java)
            mContext.startService(i)
        }
    }

}