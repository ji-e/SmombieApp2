package com.example.smombieapp.utils

import com.example.smombieapp.base.BaseApplication
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ExcelUtil {
    val fileDir = BaseApplication.DIR
    val fileName = "${BaseApplication.FILE_PATH_NAME}${Constants.EXCEL_FILE_NAME}.xlsx"
    val strDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    val strTime = SimpleDateFormat("yyyyMMdd_hh:mm:ss", Locale.getDefault()).format(Date())

    var wb = XSSFWorkbook()
    val sheet = wb.createSheet("smombieLog")
    /**
     * 엑셀 파일 존재 여부 확인
     * fileName: String: 파일명
     * return Boolean
     * true: 있음, false: 없음
     */
     fun fileExists(): Boolean {
        val dir = File(fileDir)
        if (!dir.isDirectory && !dir.exists()) dir.mkdirs()
        if (!dir.exists()) dir.createNewFile()

        val file = File(fileName)
        return  file.exists()
    }

     fun makeExcelFile() {
        try {
            val row = sheet.createRow(0)
            val serviceStartTime: Cell = row.createCell(0)
            serviceStartTime.setCellValue("Service Start Time")

            val serviceStopTime: Cell = row.createCell(1)
            serviceStopTime.setCellValue("Service Stop Time")

            val smombieStep: Cell = row.createCell(2)
            smombieStep.setCellValue("Smombie Step")


            val file = File(fileName)
            val os = FileOutputStream(file)
            wb.write(os)
            os.close()
            insertExcelCell()
        } catch (e: IOException) {

        }
    }

    fun insertExcelCell(){
//        if(!fileExists()){
//            makeExcelFile()
//        }
        try{

            LogUtil.e("makeExcel")
            val file = File(fileName)
            val fis = FileInputStream(file)

            wb= XSSFWorkbook(fis)
            val sheet = wb.getSheetAt(0)
//            val riterator_ED: Iterator<Row> = sheet.iterator()
            val row: Row = sheet.createRow(sheet.lastRowNum + 1)
            val serviceStartTime: Cell = row.createCell(0)
            val serviceStopTime: Cell = row.createCell(1)
            val smombieStep: Cell = row.createCell(2)
            if (sheet.lastRowNum == 0) {
//                serviceStartTime.setCellValue("Service Start Time")
//                serviceStopTime.setCellValue("Service Stop Time")
//                smombieStep.setCellValue("Smombie Step")

            }else {
                val preferences = BaseApplication.useSharedPreference
                val start = preferences.getPreferencesData(Constants.START_SERVICE_TIME)
                val stop = preferences.getPreferencesData(Constants.STOP_SERVICE_TIME)
                val step = preferences.getPreferencesData(Constants.SMOMBIE_STEP)
                serviceStartTime.setCellValue(start)
                serviceStopTime.setCellValue(stop)
                smombieStep.setCellValue(step)
            }
            val os = FileOutputStream(file)
            wb.write(os)

            os.close()
            wb.close()
            fis.close()
            LogUtil.e("끝ㅌㅌㅌ")
        }catch (e: IOException){

        }
    }

    fun readExcelFile() {
        if(!fileExists()){
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

//        Toast.makeText(this, "엑셀 가져오기가 완료되었습니다.", Toast.LENGTH_SHORT).show()
//
//        makeExcel()

    }


}