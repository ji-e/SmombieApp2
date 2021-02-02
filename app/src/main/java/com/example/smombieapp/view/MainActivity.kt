package com.example.smombieapp.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import com.example.smombieapp.R
import com.example.smombieapp.base.BaseActivity
import com.example.smombieapp.base.BaseApplication
import com.example.smombieapp.databinding.ActivityMainBinding
import com.example.smombieapp.service.StepCallback
import com.example.smombieapp.service.StepService
import com.example.smombieapp.utils.BackPressCloseHandler
import com.example.smombieapp.utils.Constants
import com.example.smombieapp.utils.LogUtil


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val backPressCloseHandler = BackPressCloseHandler(this)
    private var stepService: StepService? = null


    private lateinit var stepServiceIntent: Intent

    private val stepCallback: StepCallback = object : StepCallback {
        override fun onStepCallback(step: String) {
            binding.mainTvStepCnt.text = "${getString(R.string.main_step_cnt)} $step"
        }

        override fun onUnbindService() {
            LogUtil.e("unbind")
        }

        override fun onStopService() {
            LogUtil.e("stopService")
            binding.mainCkbService.isChecked = false
            binding.mainTvStepCnt.text = "${getString(R.string.main_step_cnt)} 0"
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            LogUtil.e("disconnected")

        }

        //서비스 바인드를 담당하는 객체의 구현체
        override fun onServiceConnected(name: ComponentName, service: IBinder) {

            val mb: StepService.StepBinder = service as StepService.StepBinder
            stepService = mb.service
            stepService!!.setCallback(stepCallback)
            LogUtil.e("connected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stepService = StepService()

        initView()
    }

    override fun onResume() {
        super.onResume()
        stepServiceIntent = Intent(this, StepService::class.java)
        binding.mainTvStepCnt.text = "${getString(R.string.main_step_cnt)} ${stepService?.stepCnt?.toInt()}"
        if (stepService?.isService == "Y") {
            bindService(stepServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            binding.mainCkbService.isChecked = true

        }
    }


    private fun initView() {
        binding.mainCkbService.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked && stepService?.isService != "Y") {
                stepServiceIntent.action = Constants.STEP_SERVICE_START
                startService(stepServiceIntent)
                bindService(stepServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            } else if (!isChecked && stepService?.isService == "Y") {
                stepServiceIntent.action = Constants.STEP_SERVICE_STOP
                startService(stepServiceIntent)
                try {
                    unbindService(serviceConnection)
                }catch (e:Exception){

                }

            }
        }
    }


    override fun onStop() {
        super.onStop()
        //액티비티를 벗어났을 땐 서비스와 액티비티의 바인드를 끊어줌 : 백그라운드에서 UI를 건들지 않게 하기 위함
        if (stepService?.isService == "Y") {
            unbindService(serviceConnection)
        }
    }

    override fun onBackPressed() {
        backPressCloseHandler.onBackPressed()
    }
}
