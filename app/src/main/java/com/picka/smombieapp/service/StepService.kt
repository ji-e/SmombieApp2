package com.picka.smombieapp.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.picka.smombieapp.R
import com.picka.smombieapp.base.BaseApplication
import com.picka.smombieapp.brodcast.*
import com.picka.smombieapp.utils.Constants
import com.picka.smombieapp.utils.DBHelper
import com.picka.smombieapp.utils.LogUtil
import com.picka.smombieapp.view.MainActivity
import java.util.*


class StepService : Service(), SensorEventListener {
    companion object {
        var isService = false           // 서비스 실행 여부
        var isReceiver = false          // 리시버 등록 여부
        var startServiceDate = ""       // 서비스 실행 시작 날짜
        var startServiceTime = ""       // 서비스 실행 시작 시간
        var endServiceTime = ""         // 서비스 실행 종료 시간
        var smombieStep = 0f            // 서비스 실행 중 걸음 수
    }

    private val mStepBinder: StepBinder = StepBinder()

    private val screenReceiver = ScreenReceiver()
    private val smombieReceiver = SmombieReceiver()

    private var sensorManager: SensorManager? = null
    private var stepCountSensor: Sensor? = null
    private var stepDetectorSensor: Sensor? = null

    private val sp = BaseApplication.useSharedPreference

    private var wm: WindowManager? = null
    private var mView: View? = null

    private var alarmMgr: AlarmManager? = null

    private var step = 0f
    private var stepCnt = 0f
    private var isScreenOn = true       // 스크린 ON/OFF 여부
    private var isLock = false          // 스크린 스몸비 잠금 여부

    private val screenCallback = object : ScreenCallback {
        override fun onScreenON(screenOn: Boolean) {
            isScreenOn = screenOn
            if (!isScreenOn) {
                hideLockScreen()
                isLock = false
            }
        }
    }

    private val countDownTimer = object : CountDownTimer(3000, 1000) {
        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            hideLockScreen()
            isLock = false
        }
    }

    internal inner class StepBinder : Binder() {
        val service: StepService
            get() = this@StepService
    }

    private var callback: StepCallback? = null
    fun setCallback(callback: StepCallback) {
        this.callback = callback
    }

    override fun onBind(intent: Intent): IBinder? {
        LogUtil.e("onBind_StepService")
        return mStepBinder
    }

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(UncaughtException())
        LogUtil.e("onCreate_StepService")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtil.e("onStartCommand_StepService", intent?.action)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepDetectorSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        stepCountSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepDetectorSensor != null || stepCountSensor != null) {
            onStartFore()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent): Boolean {
        LogUtil.e("onUnbind_StepService")
        callback?.onUnbindService()
        return super.onUnbind(intent)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
            stepChange()
        } else if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            if (step < 1) {
                step = event.values[0]
                stepCnt = event.values[0] - step
                return
            }
            stepChange()
            stepCnt = event.values[0] - step
            if (isScreenOn) {
                smombieStep++
                sp.setPreferencesData(Constants.STEP_SERVICE_STEP, smombieStep.toInt().toString())
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    /** 스몸비 시작 */
    private fun onStartFore() {
        if (sp.getPreferencesData(Constants.IS_STEP_SERVICE) == "Y") {
            startServiceDate = sp.getPreferencesData(Constants.STEP_SERVICE_DATE)
            startServiceTime = sp.getPreferencesData(Constants.STEP_SERVICE_TIME)
            smombieStep =
                if(sp.getPreferencesData(Constants.STEP_SERVICE_STEP).isEmpty()) 0f
                else sp.getPreferencesData(Constants.STEP_SERVICE_STEP).toFloat()
        } else {
            startServiceDate = BaseApplication().getDate()
            startServiceTime = BaseApplication().getDateTime()
        }

        isService = true

        sensorRegisterManager()
        screenRegisterReceiver()
        midnightRegisterAlarmManager()
        showNotification()

        sp.setPreferencesData(Constants.IS_STEP_SERVICE, "Y")
        sp.setPreferencesData(Constants.STEP_SERVICE_DATE, startServiceDate)
        sp.setPreferencesData(Constants.STEP_SERVICE_TIME, startServiceTime)
    }

    /** 스몸비 종료 */
    private fun onStopFore() {
        onDestroy()
    }

    /** 센서 등록 */
    private fun sensorRegisterManager() {
        sensorManager?.registerListener(
            this,
            stepDetectorSensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )

        sensorManager?.registerListener(
            this,
            stepCountSensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )
    }

    /** 센서 해제 */
    private fun sensorUnregisterManager() {
        sensorManager?.unregisterListener(this)
    }

    /** 스크린 리시버 등록 */
    private fun screenRegisterReceiver() {
        val screenIntentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }

        val smombieIntentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_SCREEN_OFF)
            addDataScheme("smombie")
        }
        registerReceiver(smombieReceiver, smombieIntentFilter)
        registerReceiver(screenReceiver, screenIntentFilter)
        screenReceiver.setCallback(screenCallback)
        isReceiver = true
    }

    /** 스크린 리시버 해제 */
    private fun screenUnregisterReceiver() {
        if (!isReceiver) return

        isReceiver = false
        try {
            unregisterReceiver(screenReceiver)
            unregisterReceiver(smombieReceiver)
        } catch (e: IllegalArgumentException) {
            LogUtil.e(e)
        } catch (e: Exception) {
            LogUtil.e(e)
        }
    }

    /** 자정 알람매니저 등록 */
    private fun midnightRegisterAlarmManager() {
        LogUtil.e("midnightCall")
        val alarmBroadcastReceiverIntent = Intent(this, MidnightCallReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmBroadcastReceiverIntent, 0)
        alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val resetCal = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        alarmMgr?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            resetCal.timeInMillis + AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    /** 자정 알람매니저 해제 */
    private fun midnightUnregisterAlarmManager(){
        val alarmBroadcastReceiverIntent = Intent(this, MidnightCallReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmBroadcastReceiverIntent, 0)
        alarmMgr?.cancel(pendingIntent)
    }

    /** 알림창 */
    private fun showNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentText(getString(R.string.notification_body))
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .setShowWhen(false)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // android Oreo 알림 채널이 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID,
                Constants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        startForeground(Constants.NOTIFICATION_ID, notificationBuilder.build())

        startInsert()
    }

    /** 초기화 */
    private fun reset() {
        sp.setPreferencesData(Constants.IS_STEP_SERVICE, "N")
        sp.setPreferencesData(Constants.STEP_SERVICE_STEP, "0")
        sp.removePreferencesData(Constants.STEP_SERVICE_DATE)
        sp.removePreferencesData(Constants.STEP_SERVICE_TIME)

        isService = false
        stepCnt = 0f
        step = 0f
        smombieStep = 0f

        countDownTimer.cancel()

        midnightUnregisterAlarmManager()
        sensorUnregisterManager()
        screenUnregisterReceiver()
    }

    /** 잠금화면 설정 */
    private fun showLockScreen() {
        try {
            if (!Settings.canDrawOverlays(this)) return

            isLock = true
            val type =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                }
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            )

            val inflate = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            mView = inflate.inflate(R.layout.lock_screen_view, null, false)

            if (wm != null && mView != null) {
                val lockScreenBtnCall = mView!!.findViewById<TextView>(R.id.lock_btn_emergency)
                lockScreenBtnCall.setOnClickListener {
                    // 1분동안 잠금화면 풀기
                    LogUtil.e("긴급해제")
                    isLock = true
                    countDownTimer.cancel()
                    wm!!.removeView(mView)
                    mView = null
                    wm = null
                    Handler(Looper.getMainLooper()).postDelayed({
                        isLock = false
                    }, 1000 * 60)

                }

                wm?.addView(mView, params)
            }
        } catch (e: Exception) {
            LogUtil.e(e)
        }

    }

    /** 잠금화면 해제 */
    private fun hideLockScreen() {
        countDownTimer.cancel()
        if (wm != null && mView != null) {
            wm?.removeView(mView)
        }

        mView = null
        wm = null
    }

    /** 걸음수 변화 체크*/
    private fun stepChange() {
        countDownTimer.cancel()
        countDownTimer.start()

        if (isScreenOn && !isLock) {
            showLockScreen()
        }

        if (mView != null) {
            val lockScreenStepCnt = mView!!.findViewById<TextView>(R.id.lock_tv_step_cnt)
            val lockScreenBtnCall = mView!!.findViewById<TextView>(R.id.lock_btn_emergency)
            val lockScreenTvTitle = mView!!.findViewById<TextView>(R.id.lock_tv_title)
            val lockScreenTvSubTitle = mView!!.findViewById<TextView>(R.id.lock_tv_subtitle)
            var step = "${smombieStep.toInt()}"
            when {
                smombieStep.toInt() < 10 -> {
                    step = "0${smombieStep.toInt()}"
                }
                smombieStep.toInt() > 99 -> {
                    lockScreenStepCnt.setTextColor(getColor(R.color.color_primary_red))
                    lockScreenBtnCall.setTextColor(getColor(R.color.color_primary_red))
                    lockScreenTvTitle.text = getString(R.string.lock_screen_view_title2)
                    lockScreenTvSubTitle.text = getString(R.string.lock_screen_view_subtitle2)
                }
                else -> {
                    step = "${smombieStep.toInt()}"
                }
            }

            lockScreenStepCnt.text = step
        }
    }

    /** Smombie DB Update */
    private fun endUpdate() {
        DBHelper(this).update(
            startServiceDate,
            startServiceTime,
            endServiceTime,
            BaseApplication().getTime(),
            smombieStep.toInt().toString()
        )
    }

    /** 시작시간 DB Insert */
    private fun startInsert() {
        DBHelper(this).delete(startServiceTime)
        DBHelper(this).insert(
            startServiceDate,
            startServiceTime,
            "",
            "",
            "0")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.e("onDestroy_StepService")
        stopForeground(true)
        endServiceTime = BaseApplication().getDateTime()
        endUpdate()
        reset()
        callback?.onEndService()
        LogUtil.e("!!onDestroy_StepService!!")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        LogUtil.e("onTaskRemoved_StepService")
    }

    inner class UncaughtException : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            LogUtil.e(t, e)
            onDestroy()
        }
    }

}