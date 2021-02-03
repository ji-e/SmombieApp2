package com.example.smombieapp.utils

import android.widget.Toast
import com.example.smombieapp.R
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
    private fun fileExists(): Boolean {
        val dir = File(fileDir)
        if (!dir.isDirectory && !dir.exists()) dir.mkdirs()
        if (!dir.exists()) dir.createNewFile()

        val file = File(fileName)
        return  file.exists()
    }

    private fun makeExcelFile() {
        val row = sheet.createRow(0)
        val cell = row.createCell(0)
        try {
            val file = File(fileName)
            val os = FileOutputStream(file)
            wb.write(os)
            os.close()

        } catch (e: IOException) {

        }
    }

    fun insertExcelCell(){
        if(!fileExists()){
            makeExcelFile()
            return
        }
        try{
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