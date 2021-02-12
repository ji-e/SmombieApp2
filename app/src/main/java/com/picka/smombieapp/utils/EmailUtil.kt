package com.picka.smombieapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.picka.smombieapp.BuildConfig
import com.picka.smombieapp.base.BaseApplication
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class EmailUtil {


    fun sendEmailExcel(
            mContext: Context,
            title: String?,
            receivers: Array<String>) {


        val sendFilePath = "${BaseApplication.FILE_PATH_NAME}${Constants.EXCEL_FILE_NAME}.xlsx"

        val file = File(sendFilePath)
        var fileUri: Uri? = null

        if (!file.exists()) {
            LogUtil.e("엑셀 파일 없음")
        } else {

            fileUri =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        FileProvider.getUriForFile(mContext, "com.picka.smombieapp", file)
                    } else {
                        Uri.fromFile(file)
                    }
        }

        val sb = StringBuffer()
        sb.append("내용: ")

        val email = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_EMAIL, receivers)
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            if (fileUri != null) {
                putExtra(Intent.EXTRA_STREAM, fileUri)
            }

        }
        email.type = "message/rfc822"

        mContext.startActivity(email)
    }

    fun sendEmailToAdmin(
            mContext: Context,
            title: String?,
            receivers: Array<String>
    ) {

        val strDate = SimpleDateFormat("yyyyMM", Locale.getDefault()).format(Date())
        val sendFilePath = BaseApplication.FILE_PATH_NAME + "DebugLog_$strDate.txt"
        val file = File(sendFilePath)
        var fileUri: Uri? = null

        if (!file.exists()) {
            LogUtil.e("로그 파일 없음")
        } else {

            fileUri =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        FileProvider.getUriForFile(mContext, "com.picka.smombieapp", file)
                    } else {
                        Uri.fromFile(file)
                    }
        }

        val sb = StringBuffer()
        sb.append("App version: ${BuildConfig.VERSION_NAME}\n")
        sb.append("Device Brand: ${Build.BRAND}\n")
        sb.append("Device Model: ${Build.MODEL}\n")
        sb.append("Android SDK: ${Build.VERSION.SDK_INT}\n")
        sb.append("Device Brand: ${Build.BRAND}(${Build.VERSION.RELEASE})\n")
        sb.append("내용: ")

        val email = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_EMAIL, receivers)
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            if (fileUri != null) {
                putExtra(Intent.EXTRA_STREAM, fileUri)
            }

        }
        email.type = "message/rfc822"

        mContext.startActivity(email)
    }
}