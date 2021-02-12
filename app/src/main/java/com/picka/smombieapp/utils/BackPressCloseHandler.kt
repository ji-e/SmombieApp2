package com.picka.smombieapp.utils

import android.app.Activity
import android.content.Context
import com.picka.smombieapp.R

/**
 * create by jieun
 *
 * back key 두번 클릭 시 앱 종료
 * mContext: Context
 */
class BackPressCloseHandler(private val mContext: Context) {
    private var backKeyPressedTime: Long = 0
    private val toast by lazy {
        CustomToast(mContext).makeToast(mContext.getString(R.string.exit_msg))
    }

    fun onBackPressed() {
        // 한번 눌렀을 때
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            toast.show()
            return
        }

        // 2초 안에 두번 눌렀을 때
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            (mContext as Activity).finishAffinity()
//            android.os.Process.killProcess(android.os.Process.myPid())
            toast.cancel()
        }

    }

}