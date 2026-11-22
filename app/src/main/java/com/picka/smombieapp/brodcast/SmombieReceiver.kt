package com.picka.smombieapp.brodcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.picka.smombieapp.base.BaseApplication
import com.picka.smombieapp.service.StepService
import com.picka.smombieapp.utils.Constants
import com.picka.smombieapp.utils.LogUtil

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
            try {
                ContextCompat.startForegroundService(mContext, i)
            } catch (e: Exception) {
                // Android 12+는 백그라운드에서 포그라운드 서비스 시작을 막는다
                LogUtil.e(e)
            }
        }
    }

}