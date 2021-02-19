package com.picka.smombieapp.brodcast

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.database.getStringOrNull
import com.picka.smombieapp.R
import com.picka.smombieapp.base.BaseApplication
import com.picka.smombieapp.service.StepService
import com.picka.smombieapp.utils.Constants
import com.picka.smombieapp.utils.DBHelper
import com.picka.smombieapp.utils.LogUtil
import com.picka.smombieapp.view.MainActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class MidnightCallReceiver : BroadcastReceiver() {
    private var alarmMgr: AlarmManager? = null
    override fun onReceive(context: Context, intent: Intent) {
        LogUtil.e("midnightCallReceive")
        Thread.setDefaultUncaughtExceptionHandler(UncaughtException())
        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        midnightUnregisterAlarmManager(context)
        StepService.endServiceTime = BaseApplication().getDateTime()
        endUpdate(context)
        showNotification(context)
        midnightRegisterAlarmManager(context)
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
                .setAutoCancel(true)

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

        dataResetCheck(context)
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

    /** 2주 후 데이터 초기화*/
    private fun dataResetCheck(context: Context) {
        try {
            val db = DBHelper(context)
            val cursor = db.selectAll()

            if (cursor.count > 5) {
                cursor.moveToFirst()
                val format = SimpleDateFormat("yyyyMMdd")
                val firstDate = format.parse(cursor.getStringOrNull(1))
                val currentDate = format.parse(BaseApplication().getDate())
                val calDate = firstDate.time - currentDate.time
                var calDateDays = calDate / (24 * 60 * 60 * 1000)

                calDateDays = abs(calDateDays)

                LogUtil.d(firstDate, currentDate, calDateDays)

                if (calDateDays > 14) {
                    db.onReset()
                }
            }
            startInsert(context)
        } catch (e: ParseException) {
            LogUtil.e(e)
        }
    }


    /** 자정 알람매니저 해제 */
    private fun midnightUnregisterAlarmManager(context: Context) {
        val alarmBroadcastReceiverIntent = Intent(context, MidnightCallReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmBroadcastReceiverIntent, 0)
        alarmMgr?.cancel(pendingIntent)
    }

    /** 자정 알람매니저 등록 */
    private fun midnightRegisterAlarmManager(context: Context) {
        LogUtil.e("midnightCallRegistedAdd")
        val alarmBroadcastReceiverIntent = Intent(context, MidnightCallReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmBroadcastReceiverIntent, 0)

        val resetCal = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.HOUR_OF_DAY, 24)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        alarmMgr?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            resetCal.timeInMillis,
            pendingIntent
        )
    }

    inner class UncaughtException : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            LogUtil.e(t, e)
        }
    }
}