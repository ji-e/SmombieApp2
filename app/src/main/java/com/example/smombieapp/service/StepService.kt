package com.example.smombieapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.core.app.NotificationCompat
import com.example.smombieapp.R
import com.example.smombieapp.base.BaseApplication
import com.example.smombieapp.brodcast.ScreenBR
import com.example.smombieapp.utils.Constants
import com.example.smombieapp.utils.LogUtil
import com.example.smombieapp.view.MainActivity


class StepService : Service(), SensorEventListener {
    private val mStepBinder: StepBinder = StepBinder()
    private val screenBR = ScreenBR()

    private val preferences = BaseApplication.useSharedPreference
    var isService = preferences.getPreferencesData(Constants.IS_STEP_SERVICE)
    var stepCnt = if (preferences.getPreferencesData(Constants.STEP_SERVICE_CNT)
            .isEmpty()
    ) 0f else preferences.getPreferencesData(Constants.STEP_SERVICE_CNT).toFloat()
    var step = 0f
    var iss = false

    internal inner class StepBinder : Binder() {
        val service: StepService
            get() = this@StepService
    }

    //    var mStepDetector = 0
    private var callback: StepCallback? = null
    fun setCallback(callback: StepCallback) {
        this.callback = callback
    }

    override fun onBind(intent: Intent): IBinder {
        return mStepBinder
    }

    private var sensorManager: SensorManager? = null
    private var stepDetectorSensor: Sensor? = null
    private var stepCountSensor: Sensor? = null
    override fun onCreate() {
        super.onCreate()
//        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        stepDetectorSensor =
//            sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
//        if (stepDetectorSensor == null) {
//        } else {
//            sensorManager!!.registerListener(
//                this,
//                stepCountSensor,
//                SensorManager.SENSOR_DELAY_UI
//            )
//        }
//        // COUNTER
//        stepCountSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
//        if (stepCountSensor == null) {
//        } else {
//            sensorManager!!.registerListener(
//                this,
//                stepCountSensor,
//                SensorManager.SENSOR_DELAY_UI
//            )
//        }
    }

    private fun setScreenOn() {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        registerReceiver(screenBR, intentFilter);
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        if (intent.action == Constants.STEP_SERVICE_START) {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            stepCountSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            if (stepCountSensor != null) {
                sensorManager!!.registerListener(
                    this,
                    stepCountSensor,
                    SensorManager.SENSOR_DELAY_GAME
                )

                setScreenOn()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    onStartFore()
                }
            } else {
                onStopFore()
            }

        } else {
            onStopFore()
        }

        return START_NOT_STICKY
//        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent): Boolean {
//        unRegistManager()
        if (callback != null) callback!!.onUnbindService()
        return super.onUnbind(intent)
    }

    private fun unRegistManager() {
        try {
            sensorManager!!.unregisterListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val cdt = object : CountDownTimer(5000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            LogUtil.e("seconds remaining: " + millisUntilFinished / 1000)
        }

        override fun onFinish() {
            removeViewd()
        }
    }

    override fun onSensorChanged(event: SensorEvent) {

        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            if (callback != null) {
                if (step < 1) {
                    step = event.values[0]
                }
                stepCnt = event.values[0] - step

                LogUtil.e("스텝 카운트: ${event.values[0]}, $stepCnt")
                preferences.setPreferencesData(
                    Constants.STEP_SERVICE_CNT,
                    stepCnt.toInt().toString()
                )

                callback!!.onStepCallback(stepCnt.toString())
                cdt.cancel()
                cdt.start()


                if (screenBR.isScreenON && !iss) {
                    iss = true
                    lock()
//                    showNotification()
                }
            }
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor,
        accuracy: Int
    ) {
    }

    private fun onStopFore() {
        LogUtil.e("스몸비 종료중")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true)
        }
        stopSelf()
    }

    private fun onStartFore() {
        preferences.setPreferencesData(Constants.IS_STEP_SERVICE, "Y")
        isService = preferences.getPreferencesData(Constants.IS_STEP_SERVICE)

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            0
        )


        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, Constants.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(getString(R.string.notification_body))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)


        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // android Oreo 알림 채널이 필요합니다
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID,
                Constants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        startForeground(Constants.NOTIFICATION_ID, notificationBuilder.build())


    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.e("스몸비 종료")

        preferences.setPreferencesData(Constants.IS_STEP_SERVICE, "N")
        preferences.setPreferencesData(Constants.STEP_SERVICE_CNT, "0")
        stepCnt = 0f
        isService = preferences.getPreferencesData(Constants.IS_STEP_SERVICE)
        if (callback != null) callback!!.onStopService()
        unRegistManager()
        unregisterReceiver(screenBR)
        cdt.cancel()

    }

    private fun showNotification() {
        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, Constants.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(getString(R.string.notification_warning))
                .setAutoCancel(true)


        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // android Oreo 알림 채널이 필요합니다
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID,
                Constants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /*알림ID */, notificationBuilder.build())
    }


    var wm: WindowManager? = null
    var mView: View? = null

    fun lock() {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,  // Android O 이상인 경우 TYPE_APPLICATION_OVERLAY 로 설정
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )
        val inflate = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mView = inflate.inflate(R.layout.lock_view, null, false)

        val btn_img = mView!!.findViewById<Button>(R.id.dfdf)
        btn_img.setOnClickListener {
            onStopFore()
            removeViewd()
        }
        wm?.addView(mView, params)

    }

    fun removeViewd() {
        iss = false
        wm?.removeView(mView)
        mView = null
        wm = null
        cdt.cancel()
    }


}