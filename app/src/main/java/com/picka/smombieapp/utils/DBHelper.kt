package com.picka.smombieapp.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * DB
 * mContext: Context
 */
class DBHelper(mContext: Context) : SQLiteOpenHelper(mContext, "smombie", null, 1) {

    /**
     * 칼럼
     * idx: primary key to autoincrement
     * date: yyyymmdd
     * startTime: HHmmss
     * endTime: HHmmss
     * totalTime: HHmmss
     * stepCnt: 걸음 수
     */
    val tableName = "tb_smombie"


    override fun onCreate(db: SQLiteDatabase) {
        val queryCreate = "create table $tableName (idx integer primary key autoincrement, date , startTime, endTime, totalTime, stepCnt)"
        db.execSQL(queryCreate)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val queryDrop = "drop table $tableName"
        db.execSQL(queryDrop)
    }


    /**
     * tb_smombie 초기화
     */
    fun onReset() {
        val db = writableDatabase
        val queryDrop = "drop table $tableName"
        db.execSQL(queryDrop)
        val queryCreate = "create table $tableName (idx integer primary key autoincrement, date , startTime, endTime, totalTime, stepCnt)"
        db.execSQL(queryCreate)
        db.close()
        LogUtil.d(queryDrop, queryCreate)
    }

    /**
     * tb_smombie 데이터 삽입
     */
    fun insert(date: String, startTime: String, endTime: String, totalTime: String, stepCnt: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("date", date)
            put("startTime", startTime)
            put("endTime", endTime)
            put("startTime", startTime)
            put("totalTime", totalTime)
            put("stepCnt", stepCnt)
        }

        db?.insert(tableName, null, values)
//        val queryInsert = "insert into $tableName (date, startTime, endTime, totalTime, stepCnt) values (?,?,?,?,?), ($date, \'$startTime\', \'$endTime\', '$totalTime', '$StepCnt')"
//        db.execSQL(queryInsert)
        db.close()
//        LogUtil.d(queryInsert)
    }

    /**
     * tb_smombie 데이터 삽입
     */
    fun insertOrReplace(date: String, startTime: String, endTime: String, totalTime: String, stepCnt: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("date", date)
            put("startTime", startTime)
            put("endTime", endTime)
            put("startTime", startTime)
            put("totalTime", totalTime)
            put("stepCnt", stepCnt)
        }

//        db?.insert(tableName, null, values)
        val queryInsert = "insert into $tableName (date, startTime, endTime, totalTime, stepCnt) values (?,?,?,?,?), ($date, \'$startTime\', \'$endTime\', '$totalTime', '$stepCnt')"
        db.execSQL(queryInsert)
        db.close()
        LogUtil.d(queryInsert)
    }

    /**
     * tb_smombie 데이터 수정
     */
    fun update(date:String, startTime: String, endTime: String, totalTime: String, stepCnt: String) {
        val db = writableDatabase
        val queryUpdate = "update $tableName " +
                "set endTime =\'$endTime\', totalTime=\'$totalTime\', stepCnt='$stepCnt' " +
                "where startTime=\'$startTime\' AND date='$date'"
        db.execSQL(queryUpdate)
//        db.close()
        LogUtil.d(queryUpdate)

        val values = ContentValues().apply {
            put("endTime", endTime)
            put("totalTime", totalTime)
            put("stepCnt", stepCnt)
        }
//        db.update(
//                tableName,
//                values,
//                "date=$date AND startTime=$startTime",
//                null)
        db.close()
    }

    /**
     * tb_smombie 데이터 삭제
     */
    fun delete(startTime: String) {
        val db = writableDatabase
        val queryDelete = "delete from $tableName where startTime=\'$startTime\'"
        db.execSQL(queryDelete)
        LogUtil.d(queryDelete)

        db.close()
    }

    /**
     * tb_smombie 데이터 삭제
     */
    fun delete(array: ArrayList<String>, index: String) {
        val db = writableDatabase
        for (i in 0 until array.size) {
            val idx = array[i]
//            var queryDelete = "delete from $tableName where $index=\'$no\'"
//            if(index.equals("no")){
            var queryDelete = "delete from $tableName where $index=$idx"
//            }
            db.execSQL(queryDelete)
            LogUtil.d(queryDelete)
        }
        db.close()
    }

    /**
     * tb_smombie 데이터 검색
     */
    fun select(date: Int): Cursor {
        val db = writableDatabase
        val querySelect = "select * from $tableName where date=\'$date\'"
        db.rawQuery(querySelect, null)
        LogUtil.d(querySelect)
        return db.rawQuery(querySelect, null)
    }

    fun select(first: Int, last: Int): Cursor {
        val db = writableDatabase
        val querySelect = "select * from $tableName where date>=\'$first\' and date<=\'$last\' order by date asc"
        db.rawQuery(querySelect, null)
        LogUtil.d(querySelect)
        return db.rawQuery(querySelect, null)
    }

    fun select(first: Int, last: Int, str: String): Cursor {
        val db = writableDatabase
        val querySelect = "select * from $tableName where date>=\'$first\' and date<=\'$last\' and title like \'%$str%\' order by date asc "
        db.rawQuery(querySelect, null)
        LogUtil.d(querySelect)
        return db.rawQuery(querySelect, null)
    }

    fun select(str: String): Cursor {
        val db = writableDatabase
        val querySelect = "select * from $tableName where title like \'%$str%\' order by date desc"
        db.rawQuery(querySelect, null)
        LogUtil.d(querySelect)
        return db.rawQuery(querySelect, null)
    }

    fun selectAll(): Cursor {
        val db = writableDatabase
        val querySelect = "select * from $tableName"
        db.rawQuery(querySelect, null)
        LogUtil.d(querySelect)
        return db.rawQuery(querySelect, null)
    }

    fun selectResent(): Cursor {
        val db = writableDatabase
        val querySelect = "select * from $tableName order by date desc limit 10"
        db.rawQuery(querySelect, null)
        LogUtil.d(querySelect)
        return db.rawQuery(querySelect, null)
    }


}