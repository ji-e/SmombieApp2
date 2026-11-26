package com.picka.smombieapp.base

import android.Manifest
import android.database.Cursor
import android.os.*
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.picka.smombieapp.utils.DBHelper
import com.picka.smombieapp.utils.LogUtil

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Thread.setDefaultUncaughtExceptionHandler(UncaughtException())

        overridePendingTransition(0, 0)
    }

    /** Android 15+ 전체화면(edge-to-edge) 강제: 상태바·내비게이션바만큼 안쪽으로 */
    override fun setContentView(view: View) {
        super.setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }


    /** 앱 종료 */
    fun exitApp() {
        finishAffinity()
        Handler(Looper.getMainLooper()).postDelayed({
            android.os.Process.killProcess(android.os.Process.myPid())
        }, 200)
    }

    /** 앱이 돌아가는 데 꼭 필요한 런타임 권한. 기록 파일은 앱 전용 폴더에 쓰므로 Android 10+는 저장소 권한이 필요 없다 */
    fun requiredPermissions(): Array<String> =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            arrayOf(Manifest.permission.ACTIVITY_RECOGNITION)
        }

    fun getLogCursor(): Cursor {
        return DBHelper(this).selectAll()
    }
    private var mLastClickTime: Long = 0
    fun onSingleClick():Boolean{
        //현재 클릭한 시간
        val currentClickTime = SystemClock.uptimeMillis()
        //이전에 클릭한 시간과 현재시간의 차이
        val elapsedTime = currentClickTime - mLastClickTime
        //마지막클릭시간 업데이트
        mLastClickTime = currentClickTime

        //내가 정한 중복클릭시간 차이를 안넘었으면 클릭이벤트 발생못하게 return
        if (elapsedTime <= MIN_CLICK_INTERVAL) return false
        return true
    }


    companion object {
        //중복클릭시간차이
        private const val MIN_CLICK_INTERVAL: Long = 600
    }

    inner class UncaughtException : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            LogUtil.e(t, e)
        }

    }
}