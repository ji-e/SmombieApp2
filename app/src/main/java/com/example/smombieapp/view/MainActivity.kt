package com.example.smombieapp.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import com.example.smombieapp.R
import com.example.smombieapp.base.BaseActivity
import com.example.smombieapp.base.BaseApplication
import com.example.smombieapp.databinding.ActivityMainBinding
import com.example.smombieapp.service.StepCallback
import com.example.smombieapp.service.StepService
import com.example.smombieapp.utils.BackPressCloseHandler
import com.example.smombieapp.utils.Constants
import com.example.smombieapp.utils.LogUtil
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


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
//        makeExcel()
        readExcelFile()
//        location()
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



    /**
     * 엑셀 파일 존재 여부 확인
     * fileName: String: 파일명, type: Boolean: 퍼미션 체크 확인?
     * return Boolean
     * true: 있음, false: 없음
     */
    val fileDir = File(BaseApplication.DIR)
    val fileName = BaseApplication.FILE_PATH_NAME + getString(R.string.app_name)+".xlsx"
    val strDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    private fun fileExists( type: Boolean): Boolean {
        val file = File(fileName)
        if (file.exists() && !type) {
            file.delete()
            return true
        } else if (file.exists() && type) {
            return true
        }
        return false
    }

    private fun makeExcel(){
            val wb = XSSFWorkbook()
            val sheet = wb.createSheet("test")
            val row = sheet.createRow(0)
            val cell = row.createCell(0)
            cell.setCellValue("tttt")

            val dir = File(BaseApplication.DIR)
        if (!dir.isDirectory && !dir.exists()) dir.mkdirs()
        if (!dir.exists()) dir.createNewFile()


        val strDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val strTime = SimpleDateFormat("yyyyMMdd_hh:mm:ss", Locale.getDefault()).format(Date())
        val file = File(BaseApplication.FILE_PATH_NAME + "exer$strDate.xlsx")

//        LogUtil.e(file)

        try{
//

            if(!fileExists(true)){
                val os = FileOutputStream(file)
            wb.write(os)
            os.close()
            }
        val myFile_ED = File(BaseApplication.FILE_PATH_NAME + "exer$strDate.xlsx")
        val inputStream_ED = FileInputStream(myFile_ED)

        val workbook_ED = XSSFWorkbook(inputStream_ED)
        val sheet_ED = workbook_ED.getSheetAt(0)
        val riterator_ED: Iterator<Row> = sheet_ED.iterator()
        val row_ED: Row = sheet_ED.createRow(sheet_ED.lastRowNum + 1)
        if (sheet_ED.lastRowNum == 0) {
        }

        val StartTime_ED: Cell = row_ED.createCell(0)
        StartTime_ED.setCellValue("your data")

        val NoofDevice_ED: Cell = row_ED.createCell(1)
        NoofDevice_ED.setCellValue("your data")

        val Device_ID_ED: Cell = row_ED.createCell(2)
        Device_ID_ED.setCellValue("your data")


        val os_ED = FileOutputStream(myFile_ED)
        workbook_ED.write(os_ED)

        os_ED.close()
        workbook_ED.close()
        inputStream_ED.close()
        }catch (e:IOException){

        }
    }

    fun readExcelFile() {
        if(!fileExists(true)){
            makeExcel()
            return
        }
        val strDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val excelFile = FileInputStream(File(BaseApplication.FILE_PATH_NAME + "exer$strDate.xlsx"))
        val workbook = XSSFWorkbook(excelFile)
        val sheet = workbook.getSheet("test")

        LogUtil.d(sheet.physicalNumberOfRows)

        val last = sheet.physicalNumberOfRows

        for (rowIndex in 0 until sheet.physicalNumberOfRows) {
            val row = sheet.getRow(rowIndex)
            if (row != null) {
                var no: String = ""
                var date: String = ""
                var title: String = ""
                var content: String = ""
                for (columnIndex in 0 until row.physicalNumberOfCells) {
                    val cell = row.getCell(columnIndex)
                    LogUtil.d(cell)
                    if (cell != null) {
                        when (columnIndex) {
                            0 -> no = cell.stringCellValue
                            1 -> date = cell.stringCellValue
//                            2 -> title = cell.stringCellValue
//                            3 -> content = cell.stringCellValue
                        }
                    }
                }
                LogUtil.d(no,date)
            }
        }
        excelFile.close()

        Toast.makeText(this, "엑셀 가져오기가 완료되었습니다.", Toast.LENGTH_SHORT).show()

    makeExcel()

    }

}
