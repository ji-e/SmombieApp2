package com.example.smombieapp.view

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.database.getStringOrNull
import com.example.smombieapp.R
import com.example.smombieapp.base.BaseActivity
import com.example.smombieapp.base.BaseApplication
import com.example.smombieapp.databinding.ActivityMainBinding
import com.example.smombieapp.service.StepCallback
import com.example.smombieapp.service.StepService
import com.example.smombieapp.utils.*
import com.example.smombieapp.view.dialog.CustomDialog
import com.example.smombieapp.view.log.LogActivity
import java.text.ParseException
import java.text.SimpleDateFormat
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
                return
            }
            val sb = service as StepService.StepBinder
            stepService = sb.service
            stepService?.setCallback(stepCallback)
            binding.mainTvStartTime.text = StepService.startServiceTime
            dataResetCheck()
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
        permissionCheck()
        stepServiceIntent = Intent(this, StepService::class.java)

        if (StepService.isService) {
            binding.mainSwService.isChecked = true
            binding.mainTvStartTime.text = StepService.startServiceTime
        } else {
            if (BaseApplication.useSharedPreference.getPreferencesData(Constants.IS_STEP_SERVICE) == "Y") {
                binding.mainSwService.isChecked = true
                binding.mainTvStartTime.text = StepService.startServiceTime
            }
        }
    }

    override fun onBackPressed() {
        backPressCloseHandler.onBackPressed()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.mainBtnLog -> startActivity(Intent(this, LogActivity::class.java))
            binding.mainBtnEmail -> {
                val email = BaseApplication.useSharedPreference.getPreferencesData(Constants.EXCEL_EMAIL)
                if (email.isEmpty()) {
                    showEmailDialog()
                } else {
                    makeExcel()
                }
            }
            binding.mainBtnBug -> {
                val address = arrayOf(Constants.BUG_EMAIL)
                EmailUtil().sendEmailToAdmin(this, getString(R.string.btn_bug), address)
            }
            binding.mainImgSetting -> {
                startActivity(Intent(this, SettingActivity::class.java))
            }
        }
    }

    private fun initView() {
        binding.mainImgSetting.setOnClickListener(this)
        binding.mainBtnLog.setOnClickListener(this)
        binding.mainBtnBug.setOnClickListener(this)
        binding.mainBtnEmail.setOnClickListener(this)
//        binding.mainEtEmail.addTextChangedListener(
//                object : TextWatcher {
//                    override fun afterTextChanged(s: Editable?) {
//
//                    }
//
//                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//                    }
//
//                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                        binding.mainBtnEmail.isEnabled = !s.isNullOrEmpty()
//                    }
//
//                }
//        )
        binding.mainSwService.setOnCheckedChangeListener { buttonView, isChecked ->
            try {
                if (stepServiceIntent != null) {
                    if (isChecked && !StepService.isService) {
                        LogUtil.e("SERVICE_START")
                        setServiceState("START")
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

        val color = ContextCompat.getColor(this, R.color.color_FFFFFF)
        binding.mainImgSetting.setColorFilter(color)
        binding.mainImgLogo.background = ShapeDrawable(OvalShape())
        binding.mainImgLogo.clipToOutline = true


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
        val customDialog = CustomDialog(this).apply {
            setBottomDialog(
                getString(R.string.dialog_title),
                getString(R.string.dialog_end_service_content),
                null,
                getString(R.string.btn_cancel),
                View.OnClickListener {
                    dismiss()
                    binding.mainSwService.isChecked = true
                },
                getString(R.string.btn_confirm),
                View.OnClickListener {
                    dismiss()
                    setServiceState("END")
                }

            )
        }
        customDialog.show()
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

    /** 이메일로 내보내기 팝업 */
    private fun showEmailDialog() {
        val customDialog = CustomDialog(this).apply {
            setBottomDialog(
                getString(R.string.dialog_title),
                getString(R.string.dialog_email_content2),
                null,
                getString(R.string.btn_confirm),
                View.OnClickListener {
                    dismiss()
                }

            )
        }
        customDialog.show()
    }

    /** Smombie 서비스 상태 설정 */
    private fun setServiceState(state: String) {
        try {
            if (state == "START") {
                startService(stepServiceIntent)
                bindService(stepServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            } else {
                stopService(stepServiceIntent)
                unbindService(serviceConnection)
            }
        } catch (e: Exception) {
            LogUtil.e(state, e)
        } catch (e: IllegalArgumentException) {
            LogUtil.e(state, e)
        }
    }


    /** 엑셀파일 생성 완료*/
    private var excelCallback = object : ExcelCallback {
        override fun onClose() {
            CustomLoading.hideLoadingBar()
            val email =
                BaseApplication.useSharedPreference.getPreferencesData(Constants.EXCEL_EMAIL)
            val address = arrayOf(email)
            EmailUtil().sendEmailExcel(this@MainActivity, getString(R.string.btn_email), address)
        }
    }

    /** 엑셀파일 생성 */
    private fun makeExcel() {

        CustomLoading.showLoadingBar(this@MainActivity)
        val cursor = getLogCursor()
        Thread {
            run {
                val excel = ExcelUtil()

                excel.setExcelCallback(excelCallback)
                excel.makeExcelFile(cursor)

            }
        }.start()

    }


    inner class UncaughtException : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            LogUtil.e(t, e)
            setServiceState("END")
        }
    }


}
