package com.example.smombieapp.utils

import android.util.Log
import com.example.smombieapp.BuildConfig
import com.example.smombieapp.base.BaseApplication
import java.io.*
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * create by jieun
 */
object LogUtil {
    private const val TAG = "LogUtil"
    private val isLog = BuildConfig.DEBUG
    private val isFLog = BuildConfig.DEBUG

    fun v(vararg text: Any?) {
        doLog(Log.VERBOSE, *text)
    }

    fun d(vararg text: Any?) {
        doLog(Log.DEBUG, *text)
    }

    fun i(vararg text: Any?) {
        doLog(Log.INFO, *text)
    }

    fun w(vararg text: Any?) {
        doLog(Log.WARN, *text)
    }

    fun e(vararg text: Any?) {
        doLog(Log.ERROR, *text)
    }

    private fun doLog(logLevel: Int, vararg logText: Any?) {
        if (!isLog) return

        val stb = StringBuilder()
        for (i in logText.indices) {
            stb.append(logText[i])
            stb.append(", \t")
        }

        var log = stb.toString()

        //take stackTrace element at index 4 because:
        //0: VMStack.getThreadStackTrace(Native Method)
        //1: java.lang.Thread.getStackTrace
        //2: LogUtil -> doLog method (this method)
        //3: LogUtil -> log method
        //4: this is the calling method!
        val stackTrace = Thread.currentThread().stackTrace
        if (stackTrace.size > 4) {
            val element = stackTrace[4]
            val fullClassName = element.className
            val simpleClassName = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)

            log = MessageFormat.format(
                "T:{0} | {1} {2}() {3}|" + "\n{4}\n",
                Thread.currentThread().id,
                simpleClassName,
                element.methodName,
                element.lineNumber,
                stb.toString()
            )
        }

        Log.println(logLevel, "FITCA", log)

        if(logLevel == Log.ERROR)
            flog(log)
    }

    /**
     * 로그파일생성
     * @log:String
     */
    private fun flog(log: String) {
        if (!isFLog) return
        try {
            val dir = File(BaseApplication.DIR)
            if (!dir.isDirectory && !dir.exists()) dir.mkdirs()
            if (!dir.exists()) dir.createNewFile()
            val strDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
            val strTime = SimpleDateFormat("yyyyMMdd_hh:mm:ss", Locale.getDefault()).format(Date())
            val file = File(BaseApplication.FILE_PATH_NAME + "DebugLog_$strDate.txt")
            val buf = BufferedWriter(OutputStreamWriter(FileOutputStream(file, true), "UTF-8"))

            if (log.length < 3600) {
                buf.append("$strTime:  $log\n")
            } else {
                buf.append("\n")
                val st = StringTokenizer("$strTime:  $log", "\n", false)
                while (st.hasMoreTokens()) {
                    val token = st.nextToken()
                    val N = token.length / 3600
                    var i = 0
                    while (i < N) {
                        buf.append(token.substring(i, i + 3600) + "\n")
                        i++
                    }
                    buf.append(
                        token.substring(i) + "\n"
                    )
                }
            }
            buf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}