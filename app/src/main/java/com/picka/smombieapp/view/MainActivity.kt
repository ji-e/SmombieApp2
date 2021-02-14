package com.picka.smombieapp.view

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.text.Html
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.database.getStringOrNull
import androidx.core.view.GravityCompat
import com.picka.smombieapp.R
import com.picka.smombieapp.base.BaseActivity
import com.picka.smombieapp.base.BaseApplication
import com.picka.smombieapp.databinding.ActivityMainBinding
import com.picka.smombieapp.service.StepCallback
import com.picka.smombieapp.service.StepService
import com.picka.smombieapp.utils.*
import com.picka.smombieapp.view.dialog.CustomDialog
import com.picka.smombieapp.view.dialog.CustomServiceEndDialog
import com.picka.smombieapp.view.dialog.CustomServiceEndListener
import com.picka.smombieapp.view.history.HistoryActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList
import kotlin.math.abs


class MainActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private val backPressCloseHandler by lazy { BackPressCloseHandler(this) }
    private var stepService: StepService? = null

    private val db by lazy { DBHelper(this) }

    private var stepServiceIntent: Intent? = null

    private val stepCallback: StepCallback = object : StepCallback {
        override fun onStepCallback(step: String) {

        }

        override fun onUnbindService() {
            LogUtil.e("UNBIND_SERVICE")
        }

        override fun onEndService() {
            LogUtil.e("END_SERVICE")
            binding.mainTvStartTime.text = getString(R.string.main_start_msg)
            binding.mainSwService.isChecked = false
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            LogUtil.e("SERVICE_DISCONNECTED")
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            LogUtil.e("SERVICE_CONNECTED")

            if (!StepService.isService) {
                binding.mainSwService.isChecked = false
                setServiceState("END")
                showCircle("END")
                return
            }
            val sb = service as StepService.StepBinder
            stepService = sb.service
            stepService?.setCallback(stepCallback)
            dataResetCheck()
            showServiceStartTime()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Thread.setDefaultUncaughtExceptionHandler(UncaughtException())

        stepService = StepService()
        initView()
    }

    override fun onResume() {
        super.onResume()
        LogUtil.e("SERVICE_STATE", StepService.isService)
        binding.mainDrawer.closeDrawer((GravityCompat.END))
        permissionCheck()
        stepServiceIntent = Intent(this, StepService::class.java)

        if (StepService.isService) {
            binding.mainSwService.isChecked = true
            showCircle("START")
        } else {
            if (BaseApplication.useSharedPreference.getPreferencesData(Constants.IS_STEP_SERVICE) == "Y") {
                binding.mainSwService.isChecked = true
            } else {
                showCircle("END")
            }
        }
    }

    override fun onBackPressed() {
        backPressCloseHandler.onBackPressed()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.mainLayMenu.menuBtnClose ->  binding.mainDrawer.closeDrawer((GravityCompat.END))
            binding.mainLayMenu.menuTvHistroy -> startActivity(Intent(this, HistoryActivity::class.java))
            binding.mainLayMenu.menuTvBug -> startActivity(Intent(this, BugActivity::class.java))
            binding.mainLayMenu.menuLayVersion -> startActivity(Intent(this, VersionActivity::class.java))
            binding.mainBtnMenu -> binding.mainDrawer.openDrawer((GravityCompat.END))
        }
    }

    private fun initView() {
        binding.mainBtnMenu.setOnClickListener(this)
        binding.mainLayMenu.menuTvHistroy.setOnClickListener(this)
        binding.mainLayMenu.menuTvBug.setOnClickListener(this)
        binding.mainLayMenu.menuBtnClose.setOnClickListener(this)
        binding.mainLayMenu.menuLayVersion.setOnClickListener(this)

        binding.mainSwService.setOnCheckedChangeListener { buttonView, isChecked ->
            try {
                if (stepServiceIntent != null) {
                    if (isChecked && !StepService.isService) {
                        LogUtil.e("SERVICE_START")
                        setServiceState("START")
                        showCircle("START")
                    } else if (!isChecked && StepService.isService) {
                        LogUtil.e("SERVICE_END")
                        showEndServiceDialog()
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.e(e)
            }
        }
    }

    /** 권한체크 */
    private fun permissionCheck() {
        val permissionList = ArrayList<String>()
        val permission: Array<String> =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            } else {
                arrayOf(
                    Manifest.permission.ACTIVITY_RECOGNITION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }

        for (i in permission.indices) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, permission[i])

            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                permissionList.add(permission[i])
            }
        }

        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager

        if (permissionList.isNotEmpty() || !Settings.canDrawOverlays(this) || !pm.isIgnoringBatteryOptimizations(
                packageName
            )
        ) {
            showPermissionDialog()
        }

    }

    /** 2주 후 데이터 초기화*/
    private fun dataResetCheck() {
        try {
            val cursor = getLogCursor()

            if (cursor.count > 0) {
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
                    db.insert(
                        StepService.startServiceDate,
                        StepService.startServiceTime,
                        "",
                        "",
                        "0"
                    )
                }
            }

        } catch (e: ParseException) {
            LogUtil.e(e)
        }
    }

    /** Smombie 종료 방지 팝업 */
    private fun showEndServiceDialog() {
        val customServiceEndListener = object : CustomServiceEndListener{
            override fun onServiceEndClick() {
                setServiceState("END")
                showCircle("END")
            }

            override fun onCloseClick() {
                binding.mainSwService.isChecked = true
            }

        }
        val customServiceEndDialog = CustomServiceEndDialog(this, customServiceEndListener)
        customServiceEndDialog.show()
    }

    /** 권한설정 팝업 */
    private fun showPermissionDialog() {
        val customDialog = CustomDialog(this).apply {
            setBottomDialog(
                getString(R.string.dialog_title),
                getString(R.string.dialog_permission_content),
                null,
                getString(R.string.btn_confirm),
                View.OnClickListener {
                    dismiss()
                    finish()
                }

            )
        }
        customDialog.show()
    }

    /** Smombie 서비스 상태 설정 */
    private fun setServiceState(isState: String) {
        try {
            if (isState == "START") {
                startService(stepServiceIntent)
                bindService(stepServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            } else {
                stopService(stepServiceIntent)
                unbindService(serviceConnection)
            }
        } catch (e: Exception) {
            LogUtil.e(isState, e)
        } catch (e: IllegalArgumentException) {
            LogUtil.e(isState, e)
        }
    }

    private fun showCircle(isState: String) {
        val isVisible: Int
        if (isState == "START") {
            isVisible = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.progress_circle)
            binding.mainProgress.startAnimation(animation)
            binding.mainImgInnerCircle.setBackgroundResource(R.drawable.img_circle2)
            binding.mainImgLogo.setBackgroundResource(R.drawable.logo_on)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.mainTvMsg.text = Html.fromHtml(getString(R.string.main_on_msg), Html.FROM_HTML_MODE_LEGACY)
            } else {
                binding.mainTvMsg.text = Html.fromHtml(getString(R.string.main_on_msg))
            }
            showServiceStartTime()
        } else {
            isVisible = View.INVISIBLE
            binding.mainProgress.clearAnimation()
            binding.mainImgInnerCircle.setBackgroundResource(R.drawable.img_circle)
            binding.mainImgLogo.setBackgroundResource(R.drawable.logo_off)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.mainTvMsg.text = Html.fromHtml(getString(R.string.main_off_msg), Html.FROM_HTML_MODE_LEGACY)
            } else {
                binding.mainTvMsg.text = Html.fromHtml(getString(R.string.main_off_msg))
            }
        }
        binding.mainTxtStartTime.visibility = isVisible
        binding.mainLayStartTime.visibility = isVisible
        binding.mainProgress.visibility = isVisible
        binding.mainImgOutCircle.visibility = isVisible
    }

    private fun showServiceStartTime(){
        val start  = StepService.startServiceTime
        if(start.length <14) return
        val M = start.substring(4,6)
        val D = start.subSequence(6,8)
        var h = start.substring(9,11)
        val m = start.substring(12,14)
        val dateP:String
        if (h.toInt() >= 12) {
            h = (h.toInt() - 12).toString()
            dateP = getString(R.string.history_pm)
        } else {
            dateP = getString(R.string.history_am)
        }
        binding.mainTvStartTime.text = "$dateP ${h}시 ${m}분, ${M}월 ${D}일"
    }

    inner class UncaughtException : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            LogUtil.e(t, e)
            setServiceState("END")
        }
    }

}
