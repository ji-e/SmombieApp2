package com.picka.smombieapp.view.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.picka.smombieapp.R
import com.picka.smombieapp.base.BaseApplication
import com.picka.smombieapp.data.HistoryData
import com.picka.smombieapp.data.HistoryData2
import com.picka.smombieapp.utils.LogUtil
import java.text.SimpleDateFormat
import java.util.*


class HistoryAdapter(
    private val mContext: Context,
    private val itemList: MutableList<HistoryData>,
    private val itemList2: MutableList<HistoryData2>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.history_tv_date)
        val time: TextView = itemView.findViewById(R.id.history_tv_time)
        val step: TextView = itemView.findViewById(R.id.history_tv_step)
        val endCnt: TextView = itemView.findViewById(R.id.history_tv_end_cnt)
        val recyclerView: RecyclerView = itemView.findViewById(R.id.history_recyclerview)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_history, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val holder = viewHolder as ViewHolder
        val item = itemList[i]
        try {
            val today = BaseApplication().getDate()
            var dDate = Date()
            dDate = Date(dDate.time + (1000 * 60 * 60 * 24 * -1))
            val dSdf = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
            val yesterday = dSdf.format(dDate)

            if (item.date == today) {
                holder.date.text = "오늘"
            } else if (item.date == yesterday) {
                holder.date.text = "어제"
            } else {
                holder.date.text = "${item.date?.substring(4, 6)}월 ${item.date?.substring(6, 8)}일"
            }

            val time = item.time?.split(":")
            if (time != null && time.size > 1) {
                if (time.size == 2) {
                    holder.time.text = "${time[0]}분 ${time[1]}초"
                } else {
                    holder.time.text = "${time[0]}시간 ${time[1]}분 ${time[2]}초"
                }
            } else {
                holder.time.text = "-"
            }
            holder.step.text = "${item.step}걸음"

            var endCnt = (item.endCnt)?.toInt()?:0

            val sublist = mutableListOf<HistoryData2>()
            for (i in 0 until itemList2.size) {
                if (item.date == itemList2[i].date) {
                    sublist.add(itemList2[i])
                    if(itemList2[i].end.isNullOrEmpty()){
                        endCnt--
                    }
                }
            }
            if (holder.time.text.toString() == "-") {
                endCnt--
            }

            holder.endCnt.text = "${endCnt}회"
            LogUtil.d(sublist)

            holder.recyclerView.layoutManager = LinearLayoutManager(mContext)
            holder.recyclerView.adapter = HistoryAdapter2(sublist)

        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
        }

    }


    override fun getItemCount(): Int {
        return itemList.size
    }
}