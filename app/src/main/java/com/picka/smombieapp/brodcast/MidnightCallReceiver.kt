package com.picka.smombieapp.brodcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.picka.smombieapp.R
import com.picka.smombieapp.base.BaseApplication
import com.picka.smombieapp.service.StepService
import com.picka.smombieapp.utils.Constants
import com.picka.smombieapp.utils.DBHelper
import com.picka.smombieapp.utils.LogUtil
import com.picka.smombieapp.view.MainActivity

class MidnightCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        LogUtil.e("midnightCall")

        StepService.endServiceTime = BaseApplication().getDateTime()
        endUpdate(context)
        showNotification(context)
    }

    /** 알림창 */
    private fun showNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val notificationBuilder =
            NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentText(context.getString(R.string.notification_body2))
                .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // android Oreo 알림 채널이 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID,
                Constants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())

    }

    /** 종료시간 DB Update */
    private fun endUpdate(context: Context) {
        DBHelper(context).update(
            StepService.startServiceDate,
            StepService.startServiceTime,
            StepService.endServiceTime,
            BaseApplication().getTime(),
            StepService.smombieStep.toInt().toString()
        )

        StepService.startServiceDate = BaseApplication().getDate()
        StepService.startServiceTime = BaseApplication().getDateTime()
        StepService.endServiceTime = BaseApplication().getDateTime()
        StepService.smombieStep = 0f

        val sp = BaseApplication.useSharedPreference
        sp.setPreferencesData(Constants.STEP_SERVICE_DATE, StepService.startServiceDate)
        sp.setPreferencesData(Constants.STEP_SERVICE_TIME, StepService.endServiceTime)
        sp.setPreferencesData(
            Constants.STEP_SERVICE_STEP,
            StepService.smombieStep.toInt().toString()
        )

        startInsert(context)
    }

    /** 시작시간 DB Insert */
    private fun startInsert(context: Context) {
        DBHelper(context).delete(StepService.startServiceTime)
        DBHelper(context).insert(
            StepService.startServiceDate,
            StepService.startServiceTime,
            "",
            "",
            "0"
        )
    }
}