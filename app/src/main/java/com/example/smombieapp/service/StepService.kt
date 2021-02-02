package com.example.smombieapp.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
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

    private var sensorManager: SensorManager? = null
    private var stepCountSensor: Sensor? = null

    private var wm: WindowManager? = null
    private var mView: View? = null

    var isService = preferences.getPreferencesData(Constants.IS_STEP_SERVICE)
    var isLock = false

    var step = 0f
    var stepCnt =
            if (preferences.getPreferencesData(Constants.STEP_SERVICE_CNT).isEmpty()) 0f
            else preferences.getPreferencesData(Constants.STEP_SERVICE_CNT).toFloat()

    private val countDownTimer = object : CountDownTimer(10 * 1000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            LogUtil.e("seconds remaining: " + millisUntilFinished / 1000)
        }

        override fun onFinish() {
            hideLockScreen()
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

    override fun onBind(intent: Intent): IBinder {
        return mStepBinder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        if (intent.action == Constants.STEP_SERVICE_START) {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            stepCountSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            if (stepCountSensor != null) {
                sensorRegisterManager()
                screenRegisterReceiver()
                onStartFore()
            } else {
                onStopFore()
            }

        } else {
            onStopFore()
        }

        return START_NOT_STICKY

    }

    override fun onUnbind(intent: Intent): Boolean {
        callback?.onUnbindService()
        return super.onUnbind(intent)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            if (callback != null) {
                if (step < 1) {
                    step = event.values[0]
                    stepCnt = event.values[0] - step
                    preferences.setPreferencesData(Constants.STEP_SERVICE_CNT, stepCnt.toInt().toString())
                    return
                }
                stepCnt = event.values[0] - step
                val stepCntStr = stepCnt.toInt().toString()
                preferences.setPreferencesData(Constants.STEP_SERVICE_CNT,stepCntStr )

                LogUtil.e("스텝 카운트: ${event.values[0]}, stepCntStr")

                callback!!.onStepCallback(stepCntStr)

                countDownTimer.cancel()
                countDownTimer.start()

                if(mView!= null){
                    lockScreenChangeStepCount(stepCntStr)
                }
                if (screenBR.isScreenON && !isLock) {
                    isLock = true
                    showLockScreen()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    /** 스몸비 시작 */
    private fun onStartFore() {
        preferences.setPreferencesData(Constants.IS_STEP_SERVICE, "Y")
        isService = preferences.getPreferencesData(Constants.IS_STEP_SERVICE)

        showNotification()
    }

    /** 스몸비 종료 */
    private fun onStopFore() {
        stopSelf()
    }

    /** 센서 등록 */
    private fun sensorRegisterManager() {
        sensorManager?.registerListener(
                this,
                stepCountSensor,
                SensorManager.SENSOR_DELAY_GAME
        )
    }

    /** 센서 해제 */
    private fun sensorUnregisterManager() {
        sensorManager?.unregisterListener(this)
    }

    /** 스크린 리시버 등록 */
    private fun screenRegisterReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        registerReceiver(screenBR, intentFilter);
    }

    /** 스크린 리시버 해제 */
    private fun screenUnregisterReceiver() {
        try {
            unregisterReceiver(screenBR)
        } catch (e: IllegalArgumentException) {
        } catch (e: Exception) {
        } finally {
        }
    }

    /** 알림창 */
    private fun showNotification(){
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val notificationBuilder =
                NotificationCompat.Builder(applicationContext, Constants.CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText(getString(R.string.notification_body))
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
    }

    /** 초기화 */
    private fun reset() {
        preferences.setPreferencesData(Constants.IS_STEP_SERVICE, "N")
        preferences.setPreferencesData(Constants.STEP_SERVICE_CNT, "0")
        isService = preferences.getPreferencesData(Constants.IS_STEP_SERVICE)
        stepCnt = 0f
        step = 0f
        countDownTimer.cancel()
        sensorUnregisterManager()
        screenUnregisterReceiver()
    }

    /** 잠금화면 설정 */
    private fun showLockScreen() {
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
        params.horizontalMargin = 10f
        params.verticalMargin = 10f
        val inflate = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mView = inflate.inflate(R.layout.lock_screen_view, null, false)

        val lockScreenBtn = mView!!.findViewById<Button>(R.id.lock_screen_btn)
        lockScreenBtn.setOnClickListener {
            onDestroy()
            hideLockScreen()
        }

        wm?.addView(mView, params)

    }

    /** 잠금화면 해제 */
    private fun hideLockScreen() {
        countDownTimer.cancel()
        wm?.removeView(mView)
        mView = null
        wm = null
        isLock = false
    }

    /** 잠금화면 걸음 수 카운트*/
    private fun lockScreenChangeStepCount(cnt:String){
        val lockScreenStepCnt = mView!!.findViewById<TextView>(R.id.lock_screen_step_cnt)
        lockScreenStepCnt.text = cnt
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.e("스몸비 종료")
        stopForeground(true)
        reset()
        callback?.onStopService()
    }

}