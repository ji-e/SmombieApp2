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
    }

    override fun onResume() {
        super.onResume()

        cursor = getLogCursor()
        setLogView()
    }

    private fun initView() {
        binding.historyBtnBack.setOnClickListener(this)
        binding.historyBtnInfo.setOnClickListener(this)
        binding.historyBtnSend.setOnClickListener(this)

        binding.historySwipe.setOnRefreshListener(this)
        binding.historySwipe.setColorSchemeResources(R.color.color_primary_blue)

    }

    private fun setLogView() {
        var date = ""
        var start = ""
        var end = ""
        var time = ""
        var step = ""
        var endCnt = 1
        var index = 0

        var historyList = mutableListOf<HistoryData>()
        val historyList2 = mutableListOf<HistoryData2>()
        val cnt = if (cursor == null) 0 else cursor?.count

        while (cursor?.moveToNext() == true) {
            val c1 = "${cursor?.getString(1)}"
            val c2 = "${cursor?.getString(2)}"
            val c3 = "${cursor?.getString(3)}"
            val c4 = "${cursor?.getString(4)}"
            val c5 = "${cursor?.getString(5)}"

            historyList2.add(
                HistoryData2(
                    c1, c2, c3, c4, c5
                )
            )

            if (index == 0) {
                date = c1
                start = c2
                end = c3
                time = c4
                step = c5
            }

            if (index > 0) {
                if (date == c1) {
                    endCnt++
                    time = BaseApplication().getTotalTime(time, c4)
                    step = (step.toInt() + c5.toInt()).toString()
                } else {
                    historyList.add(
                        HistoryData(
                            date,
                            time,
                            step,
                            endCnt.toString(),
                            null
                        )
                    )

                    date = c1
                    start = c2
                    end = c3
                    time = c4
                    step = c5
                    endCnt = 1

                }
            }

            if (cnt != null && index == (cnt - 1)) {
                historyList.add(
                    HistoryData(
                        date,
                        time,
                        step,
                        endCnt.toString(),
                        null
                    )
                )
            }
            index++
        }

        if (historyList.isNullOrEmpty()) {
            binding.historyLayEmpty.visibility = View.VISIBLE
            binding.historySwipe.visibility = View.GONE
        } else {

            val s =  ("(\\d{4})(\\d{2})(\\d{2})").toRegex().replace((historyList[0].date.toString()), "$1.$2.$3")
            val e =  ("(\\d{4})(\\d{2})(\\d{2})").toRegex().replace((historyList[historyList.size - 1].date.toString()), "$1.$2.$3")
            update()
            binding.historyTvDate.text = "$s ~ $e"
            historyList =
                (historyList.sortedByDescending { myObject -> myObject.date }).toMutableList()
            binding.historyLayEmpty.visibility = View.GONE
            binding.historySwipe.visibility = View.VISIBLE
            binding.logRecyclerview.layoutManager = LinearLayoutManager(this)
            binding.logRecyclerview.adapter = HistoryAdapter(this, historyList, historyList2)
            loadingFinishCallbackListener.onLoadingFinish(true)
        }
    }

    private fun update() {
        var dateH = SimpleDateFormat("HH", Locale.getDefault()).format(Date())
        val dateM = SimpleDateFormat("mm", Locale.getDefault()).format(Date())
        val dateP: String
        if (dateH.toInt() >= 12) {
            dateP = getString(R.string.history_pm)
            dateH = SimpleDateFormat("hh", Locale.getDefault()).format(Date())
        } else {
            dateP = getString(R.string.history_am)
        }

        binding.historyTvUpdate.text = "$dateP $dateH:$dateM, ${getString(R.string.history_update)}"

    }

    private fun showInfoDialog() {
        CustomHistoryInfoDialog(this).show()
    }


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

    override fun onRefresh() {
        binding.historySwipe.isRefreshing = true
        cursor = getLogCursor()
        setLogView()
        update()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.historyBtnInfo -> showInfoDialog()
            binding.historyBtnBack -> onBackPressed()
            binding.historyBtnSend -> showSendDialog()
        }
    }
}