package com.picka.smombieapp.utils

import android.database.Cursor
import com.picka.smombieapp.base.BaseActivity
import com.picka.smombieapp.base.BaseApplication
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ExcelUtil {
    private var excelCallback: ExcelCallback? = null
    private val fileDir = BaseApplication.DIR
    private val fileName = "${BaseApplication.FILE_PATH_NAME}${Constants.EXCEL_FILE_NAME}.xlsx"

    private var wb = XSSFWorkbook()
    private val sheet = wb.createSheet("smombieLog")

    /** 엑셀 파일 존재 여부 확인 */
    private fun fileExists() {
        val dir = File(fileDir)
        if (!dir.isDirectory && !dir.exists()) dir.mkdirs()
        if (!dir.exists()) dir.createNewFile()

        val file = File(fileName)

        if (file.exists()) {
            file.delete()
            LogUtil.d("FILE_DELETE")
        }
    }

    /** 엑셀 파일 생성 */
    fun makeExcelFile(cursor: Cursor) {
        try {
            fileExists()
//            val row = sheet.createRow(0)
//            val serviceStartTime: Cell = row.createCell(0)
//            serviceStartTime.setCellValue("서비스 시작 시간")
//
//            val serviceEndTime: Cell = row.createCell(1)
//            serviceEndTime.setCellValue("서비스 종료 시간")
//
//            val smombieTime: Cell = row.createCell(2)
//            smombieTime.setCellValue("서비스 실행 시간")
//
//            val smombieStep: Cell = row.createCell(3)
//            smombieStep.setCellValue("서비스 실행 중 걸음 수")
//
//
//            val file = File(fileName)
//            val os = FileOutputStream(file)
//            wb.write(os)
//            os.close()
//            insertExcelCell()
            LogUtil.e("makeExcelFile")
            val headerFont = wb.createFont().apply {
                bold = true
                color = IndexedColors.BLUE_GREY.getIndex()
            }

            val headerCellStyle = wb.createCellStyle().apply {
                setFont(headerFont)
            }
            val row = sheet.createRow(0)
            val columns = arrayOf("", "서비스 시작 시간", "서비스 종료 시간", "서비스 실행 시간", "서비스 실행 중 걸음 수")

            for (i in columns.indices) {
                val cell = row.createCell(i)
                cell.setCellValue(columns[i])
                cell.cellStyle = headerCellStyle
            }

            val file = File(fileName)
            val os = FileOutputStream(file)
            wb.write(os)
            os.close()
            insertExcelCell(cursor)
        } catch (e: IOException) {
            LogUtil.e(e)
        }
    }

    /** 엑셀 파일 데이터 삽입 */
    private fun insertExcelCell(cursor: Cursor) {
        try {
            LogUtil.d("insertExcelCell")

            val file = File(fileName)
            val fis = FileInputStream(file)

            wb = XSSFWorkbook(fis)
            val sheet = wb.getSheetAt(0)
            var rowNum = 1
            while (cursor.moveToNext()) {
                val row = sheet.createRow(rowNum++)
//                row.createCell(0).setCellValue(cursor.getInt(0).toString())
                row.createCell(1).setCellValue(cursor.getString(1))
                row.createCell(2).setCellValue(cursor.getString(2))
                row.createCell(3).setCellValue(cursor.getString(3))
                row.createCell(4).setCellValue(cursor.getString(4))
                row.createCell(5).setCellValue(cursor.getString(5))
            }

            sheet.setColumnWidth(0, 1000)
            sheet.setColumnWidth(1, 5000)
            sheet.setColumnWidth(2, 7000)
            sheet.setColumnWidth(3, 7000)
            sheet.setColumnWidth(4, 7000)

            val os = FileOutputStream(file)
            wb.write(os)
            os.close()
            wb.close()
            fis.close()

            excelCallback?.onClose()

        } catch (e: IOException) {
            LogUtil.e(e)
        }
    }

    fun setExcelCallback(callback: ExcelCallback) {
        excelCallback = callback
    }

//    fun makeExcelFile(cursor: Cursor) {
//        try {
//            fileExists()
//            val row = sheet.createRow(0)
//            val serviceStartTime: Cell = row.createCell(0)
//            serviceStartTime.setCellValue("서비스 시작 시간")
//
//            val serviceEndTime: Cell = row.createCell(1)
//            serviceEndTime.setCellValue("서비스 종료 시간")
//
//            val smombieTime: Cell = row.createCell(2)
//            smombieTime.setCellValue("서비스 실행 시간")
//
//            val smombieStep: Cell = row.createCell(3)
//            smombieStep.setCellValue("서비스 실행 중 걸음 수")
//
//
//            val file = File(fileName)
//            val os = FileOutputStream(file)
//            wb.write(os)
//            os.close()
//            insertExcelCell()
//
//        } catch (e: IOException) {
//
//        }
//    }
//    private fun insertExcelCell(cursor: Cursor) {
////        if(!fileExists()){
////            makeExcelFile()
////        }
//        try {
//
//            LogUtil.d("insertExcelCell")
//            val file = File(fileName)
//            val fis = FileInputStream(file)
//
//            wb = XSSFWorkbook(fis)
//            val sheet = wb.getSheetAt(0)
//            val riterator_ED: Iterator<Row> = sheet.iterator()
//            val row: Row = sheet.createRow(sheet.lastRowNum + 1)
//            val serviceStartTime: Cell = row.createCell(0)
//            val serviceStopTime: Cell = row.createCell(1)
//            val smombieStep: Cell = row.createCell(2)
//            if (sheet.lastRowNum == 0) {
////                serviceStartTime.setCellValue("Service Start Time")
////                serviceStopTime.setCellValue("Service Stop Time")
////                smombieStep.setCellValue("Smombie Step")
//
//            } else {
//                val preferences = BaseApplication.useSharedPreference
//                val start = StepService.startServiceTime
//                val stop = StepService.endServiceTime
//                val step = StepService.smombieStep.toInt().toString()
//                serviceStartTime.setCellValue(start)
//                serviceStopTime.setCellValue(stop)
//                smombieStep.setCellValue(step)
//            }
//            val os = FileOutputStream(file)
//            wb.write(os)
//
//            os.close()
//            wb.close()
//            fis.close()
//            LogUtil.e("끝ㅌㅌㅌ")
//
//
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }
//    fun readExcelFile() {
////        if (!fileExists()) {
////            return
////        }
//        val strDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
//        val excelFile = FileInputStream(File(BaseApplication.FILE_PATH_NAME + "exer$strDate.xlsx"))
//        val workbook = XSSFWorkbook(excelFile)
//        val sheet = workbook.getSheet("test")
//
//        LogUtil.d(sheet.physicalNumberOfRows)
//
//        val last = sheet.physicalNumberOfRows
//
//        for (rowIndex in 0 until sheet.physicalNumberOfRows) {
//            val row = sheet.getRow(rowIndex)
//            if (row != null) {
//                var no: String = ""
//                var date: String = ""
//                var title: String = ""
//                var content: String = ""
//                for (columnIndex in 0 until row.physicalNumberOfCells) {
//                    val cell = row.getCell(columnIndex)
//                    LogUtil.d(cell)
//                    if (cell != null) {
//                        when (columnIndex) {
//                            0 -> no = cell.stringCellValue
//                            1 -> date = cell.stringCellValue
////                            2 -> title = cell.stringCellValue
////                            3 -> content = cell.stringCellValue
//                        }
//                    }
//                }
//                LogUtil.d(no, date)
//            }
//        }
//        excelFile.close()
//
//    }



}