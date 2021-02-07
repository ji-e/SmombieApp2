package com.example.smombieapp.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import com.example.smombieapp.base.BaseActivity
import com.example.smombieapp.databinding.ActivityMainBinding
import com.example.smombieapp.service.StepCallback
import com.example.smombieapp.service.StepService
import com.example.smombieapp.utils.*
import java.util.*


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val backPressCloseHandler by lazy { BackPressCloseHandler(this) }
    private var stepService: StepService? = null

    private val db by lazy { DBHelper(this)}


    private lateinit var stepServiceIntent: Intent

    private val stepCallback: StepCallback = object : StepCallback {
        override fun onStepCallback(step: String) {
//            binding.mainTvStepCnt.text = "${getString(R.string.main_step_cnt)} $step"
        }

        override fun onUnbindService() {
            LogUtil.e("unbind")
        }

        override fun onStopService() {
            LogUtil.e("stopService")
            stopUpdate()
//            makeExcel()

//            binding.mainCkbService.isChecked = false
//            binding.mainTvStepCnt.text = "${getString(R.string.main_step_cnt)} 0"
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
            startInsert()
            LogUtil.e("connected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        stepService = StepService()

        initView()
        Thread.setDefaultUncaughtExceptionHandler(exxx())

//        makeExcel()
//        readExcelFile()
//        location()
    }

    override fun onResume() {
        super.onResume()
        LogUtil.d(StepService.isService)
        stepServiceIntent = Intent(this, StepService::class.java)
//        binding.mainTvStepCnt.text = "${getString(R.string.main_step_cnt)} ${stepService?.stepCnt?.toInt()}"
        if (StepService.isService) {
            binding.mainCkbService.isChecked = true

        }
    }


    private fun initView() {
        getDayList()
        binding.mainCkbService.setOnCheckedChangeListener { buttonView, isChecked ->
            try {
                if (isChecked && !StepService.isService) {
                    startService(stepServiceIntent)
                    bindService(stepServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
                } else if (!isChecked && StepService.isService) {
//                    makeExcel()
                    stopService(stepServiceIntent)
                    unbindService(serviceConnection)
                }
            } catch (e: Exception) {

            }
        }
        binding.mainBtnLog.setOnClickListener {
            startActivity(Intent(this, LogActivity::class.java))
        }


//        val mDPM: DevicePolicyManager
//        val mAdminName: ComponentName
//
//        mDPM = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
//        mAdminName = ComponentName(this, AmayalockAdminReceiver::class.java)
//
//        if (!mDPM.isAdminActive(mAdminName)) {
//            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
//            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName)
//            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "안전한 환경을 위해 실행 버튼을 클릭해주세요")
//            startActivityForResult(intent, 0)
//        }
    }


    override fun onStop() {
        super.onStop()
        //액티비티를 벗어났을 땐 서비스와 액티비티의 바인드를 끊어줌 : 백그라운드에서 UI를 건들지 않게 하기 위함
//        try {
//
//        } catch (e:java.lang.Exception)
        if (StepService.isService) {
//            unbindService(serviceConnection)
        }
    }

    override fun onBackPressed() {
        backPressCloseHandler.onBackPressed()
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

    inner class exxx : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            LogUtil.e(t, e)
//            makeExcel()
        }

    }

    fun getDayList() {
        sfdsdf()
        var cursor = db.selectAll()


        while (cursor.moveToNext()) {
            LogUtil.d(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5))
        }
    }

    fun sfdsdf() {
//        DBHelper(this).insert("1", "@", "3", "$", "%")
    }


   private fun startInsert(){
       val strDate = StepService.startServiceDate
       val strTime = StepService.startServiceTime
        db.insert(strDate,strTime,"","","")
    }

    private fun stopUpdate(){
        val strDate = StepService.startServiceDate
        val strStartTime = StepService.startServiceTime
        val strEndTime = StepService.endServiceTime
        val strStep = StepService.smombieStep.toInt().toString()
        db.update(strDate,strStartTime, strEndTime,"총시간",strStep)
    }
}
