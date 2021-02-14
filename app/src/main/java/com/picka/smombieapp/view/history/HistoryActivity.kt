package com.picka.smombieapp.view.history

import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.picka.smombieapp.R
import com.picka.smombieapp.base.BaseActivity
import com.picka.smombieapp.base.BaseApplication
import com.picka.smombieapp.data.HistoryData
import com.picka.smombieapp.data.HistoryData2
import com.picka.smombieapp.databinding.ActivityHistoryBinding
import com.picka.smombieapp.utils.*
import com.picka.smombieapp.view.CustomLoading
import com.picka.smombieapp.view.dialog.*
import java.text.SimpleDateFormat
import java.util.*


class HistoryActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private lateinit var binding: ActivityHistoryBinding
    private var cursor: Cursor? = null

    private var email = ""

    private val loadingFinishCallbackListener = object : LoadingFinishCallbackListener {
        override fun onLoadingFinish(isLoadingFinish: Boolean) {
            Handler(Looper.getMainLooper()).postDelayed(Runnable { //해당 어댑터를 서버와 통신한 값이 나오면 됨
                if (isLoadingFinish)
                    binding.historySwipe.isRefreshing = false
            }, 2000)
        }
    }

    interface LoadingFinishCallbackListener {
        fun onLoadingFinish(isLoadingFinish: Boolean)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Thread.setDefaultUncaughtExceptionHandler(UncaughtException())


        initView()
        cursor = getLogCursor()
        setHistoryList()
    }

    override fun onClick(v: View?) {
        if (!onSingleClick()) return
        when (v) {
            binding.historyBtnInfo -> showInfoDialog()
            binding.historyBtnBack -> onBackPressed()
            binding.historyBtnSend -> showSendDialog()
        }
    }

    override fun onRefresh() {
        binding.historySwipe.isRefreshing = true
        cursor = getLogCursor()
        setHistoryList()
        update()
    }

    private fun initView() {
        binding.historyBtnBack.setOnClickListener(this)
        binding.historyBtnInfo.setOnClickListener(this)
        binding.historyBtnSend.setOnClickListener(this)

        binding.historySwipe.setOnRefreshListener(this)
        binding.historySwipe.setColorSchemeResources(R.color.color_primary_blue)

    }

    /** 기록리스트 세팅**/
    private fun setHistoryList() {
        var historyList = mutableListOf<HistoryData>()
        val historyList2 = mutableListOf<HistoryData2>()
        val cnt = if (cursor == null) 0 else cursor?.count
        var preDate = ""
        var preTime = ""
        var preStep = ""
        var preEndCnt = 1
        var index = 0

        while (cursor?.moveToNext() == true) {
            val date = "${cursor?.getString(1)}"
            val startTime = "${cursor?.getString(2)}"
            val endTime = "${cursor?.getString(3)}"
            val time = "${cursor?.getString(4)}"
            val step = "${cursor?.getString(5)}"

            historyList2.add(HistoryData2(
                date, startTime, endTime, time, step
            ))

            if (index == 0) {
                preDate = date
                preTime = time
                preStep = step
            }

            if (index > 0) {
                if (preDate == date) {
                    preEndCnt++
                    preTime = BaseApplication().getTotalTime(preTime, time)
                    preStep = StringFormatUtil().sum(preStep, step)
                } else {
                    historyList.add(HistoryData(
                        preDate, preTime, preStep, preEndCnt.toString(), null
                    ))
                    preDate = date
                    preTime = time
                    preStep = step
                    preEndCnt = 1
                }
            }

            if (cnt != null && index == (cnt - 1)) {
                historyList.add(HistoryData(
                    preDate, preTime, preStep, preEndCnt.toString(), null
                ))
            }
            index++
        }

        if (historyList.isNullOrEmpty()) {
            binding.historyLayEmpty.visibility = View.VISIBLE
            binding.historySwipe.visibility = View.GONE
        } else {
            binding.historyLayEmpty.visibility = View.GONE
            binding.historySwipe.visibility = View.VISIBLE

            update()
            val date1 = DateFormatUtil().getDotDate(historyList[0].date.toString())
            val date2 = DateFormatUtil().getDotDate(historyList[historyList.size - 1].date.toString())
            binding.historyTvDate.text = "$date1 ~ $date2"

            historyList = (historyList.sortedByDescending { myObject -> myObject.date }).toMutableList()

            binding.logRecyclerview.layoutManager = LinearLayoutManager(this)
            binding.logRecyclerview.adapter = HistoryAdapter(this, historyList, historyList2)
            loadingFinishCallbackListener.onLoadingFinish(true)
        }
    }

    /** 새로고침 날짜 업데이트 */
    private fun update() {
        var dateH = SimpleDateFormat("HH", Locale.getDefault()).format(Date())
        val dateM = SimpleDateFormat("mm", Locale.getDefault()).format(Date())
        dateH = DateFormatUtil().getAMPMTime(dateH)

        binding.historyTvUpdate.text = "$dateH:$dateM, ${getString(R.string.history_update)}"

    }

    /** 기록 안내 다이얼로그 */
    private fun showInfoDialog() {
        CustomHistoryInfoDialog(this).show()
    }

    /** 내보내기 다이얼로그 */
    private fun showSendDialog() {
        val customHistorySendListener = object : CustomHistorySendListener {
            override fun onSendEmail(e: String) {
               email = e
                makeExcel()
            }
        }
        val customHistorySendDialog = CustomHistorySendDialog(this, customHistorySendListener)
        customHistorySendDialog.show()

    }

    /** 엑셀파일 생성 완료*/
    private var excelCallback = object : ExcelCallback {
        override fun onClose() {
            CustomLoading.hideLoadingBar()
            val address = arrayOf(email)
            EmailUtil().sendEmailExcel(this@HistoryActivity, getString(R.string.btn_email), address)
        }
    }

    /** 엑셀파일 생성 */
    private fun makeExcel() {
        CustomLoading.showLoadingBar(this)
        val cursor = getLogCursor()
        Thread {
            run {
                val excel = ExcelUtil()

                excel.setExcelCallback(excelCallback)
                excel.makeExcelFile(cursor)

            }
        }.start()

    }


    inner class UncaughtException : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            LogUtil.e(t, e)
        }

    }
}