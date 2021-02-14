package com.picka.smombieapp.view.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.picka.smombieapp.R
import com.picka.smombieapp.data.HistoryData
import com.picka.smombieapp.data.HistoryData2
import com.picka.smombieapp.utils.DateFormatUtil
import com.picka.smombieapp.utils.LogUtil


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

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as ViewHolder
        val item = itemList[position]
        try {
            holder.date.text = DateFormatUtil().getDateMD("${item.date}")
            holder.time.text = DateFormatUtil().getTimeHMS("${item.time}")
            holder.step.text = "${item.step}걸음"

            var endCnt = (item.endCnt)?.toInt() ?: 0

            val sublist = mutableListOf<HistoryData2>()
            for (i in 0 until itemList2.size) {
                if (item.date == itemList2[i].date) {
                    sublist.add(itemList2[i])
                    if (itemList2[i].end.isNullOrEmpty()) {
                        endCnt--
                    }
                }
            }
            if (holder.time.text.toString() == "-") {
                endCnt--
            }

            holder.endCnt.text = "${endCnt}회"

            holder.recyclerView.layoutManager = LinearLayoutManager(mContext)
            holder.recyclerView.adapter = HistoryAdapter2(sublist)

        } catch (e: Exception) {
//            e.printStackTrace()
            LogUtil.e(e)
        }
    }


    override fun getItemCount(): Int {
        return itemList.size
    }
}