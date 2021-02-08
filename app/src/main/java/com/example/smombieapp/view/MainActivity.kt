package com.example.smombieapp.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import com.example.smombieapp.R
import com.example.smombieapp.base.BaseActivity
import com.example.smombieapp.base.BaseApplication
import com.example.smombieapp.databinding.ActivityMainBinding
import com.example.smombieapp.service.StepCallback
import com.example.smombieapp.service.StepService
import com.example.smombieapp.utils.*
import com.example.smombieapp.view.dialog.CustomDialog


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val backPressCloseHandler by lazy { BackPressCloseHandler(this) }
    private var stepService: StepService? = null

    private val db by lazy { DBHelper(this) }

    private lateinit var stepServiceIntent: Intent

    private val stepCallback: StepCallback = object : StepCallback {
        override fun onStepCallback(step: String) {

        }

        override fun onUnbindService() {
            LogUtil.e("UNBIND_SERVICE")
        }

        override fun onEndService() {
            LogUtil.e("END_SERVICE")
            binding.mainSwService.isChecked = false
            endUpdate()
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            LogUtil.e("SERVICE_DISCONNECTED")
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            LogUtil.e("SERVICE_CONNECTED")
            val sb = service as StepService.StepBinder
            stepService = sb.service
            stepService?.setCallback(stepCallback)
            binding.mainTvStartTime.text = StepService.startServiceTime
            startInsert()

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

        stepServiceIntent = Intent(this, StepService::class.java)

        if (StepService.isService) {
            binding.mainSwService.isChecked = true
            binding.mainTvStartTime.text = StepService.startServiceTime
        }
    }

    override fun onBackPressed() {
        backPressCloseHandler.onBackPressed()
    }

    private fun initView() {
        binding.mainSwService.setOnCheckedChangeListener { buttonView, isChecked ->
            try {
                if (isChecked && !StepService.isService) {
                    LogUtil.e("SERVICE_START")
                    stepServiceIntent.action = Constants.STEP_SERVICE_START
                    setServiceState("START")
                } else if (!isChecked && StepService.isService) {
                    LogUtil.e("SERVICE_END")
                    stepServiceIntent.action = Constants.STEP_SERVICE_END
                    showEndServiceDialog()
                }
            } catch (e: Exception) {

            }
        }

        binding.mainBtnLog.setOnClickListener {
            startActivity(Intent(this, LogActivity::class.java))
        }

        binding.mainBtnBug.setOnClickListener {
            val address = arrayOf(Constants.BUG_EMAIL)
            EmailUtil().sendEmailToAdmin(this, getString(R.string.btn_bug), address)
        }
    }


    /** 시작시간 DB Insert */
    private fun startInsert() {
        val strDate = StepService.startServiceDate
        val strTime = StepService.startServiceTime
        db.insert(strDate, strTime, "", "", "")
    }

    /** Smombie DB Update */
    private fun endUpdate() {
        val strDate = StepService.startServiceDate
        val strStartTime = StepService.startServiceTime
        val strEndTime = StepService.endServiceTime
        val strStep = StepService.smombieStep.toInt().toString()
        db.update(strDate, strStartTime, strEndTime, BaseApplication().getTime(), strStep)
    }

    /** Smombie 종료 방지 팝업 */
    private fun showEndServiceDialog() {
        val customDialog = CustomDialog(this).apply {
            setBottomDialog(
                    getString(R.string.dialog_title),
                    getString(R.string.dialog_content),
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

    private fun setServiceState(state: String) {
        try {
            if (state == "START") {
                startService(stepServiceIntent)
                bindService(stepServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            } else {
                startService(stepServiceIntent)
                unbindService(serviceConnection)
            }
        } catch (e: Exception) {
        } catch (e: IllegalArgumentException) {
        }
    }

    private var excelCallback = object : ExcelCallback {
        override fun onClose() {
//            CustomLoading.hideLoadingBar()
        }
    }

    var t = Thread()
    fun makeExcel() {
        CustomLoading.showLoadingBar(this@MainActivity)
        t = Thread {
            run {
                val excell = ExcelUtil()

                excell.setExcellCallback(excelCallback)
                if (excell.fileExists()) {
                    excell.insertExcelCell()
                } else {
                    excell.makeExcelFile()
                }

            }
        }

        t.start()
    }


    inner class UncaughtException : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            LogUtil.e(t, e)
            setServiceState("END")
        }

    }

}
