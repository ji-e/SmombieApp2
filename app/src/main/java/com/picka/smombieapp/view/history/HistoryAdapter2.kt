package com.picka.smombieapp.view.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.picka.smombieapp.R
import com.picka.smombieapp.data.HistoryData2
import com.picka.smombieapp.utils.DateFormatUtil
import com.picka.smombieapp.utils.LogUtil


class HistoryAdapter2(private val itemList: MutableList<HistoryData2>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val start: TextView = itemView.findViewById(R.id.item_history_tv_start)
        val end: TextView = itemView.findViewById(R.id.item_history_tv_end)
        val time: TextView = itemView.findViewById(R.id.item_history_tv_time)
        val step: TextView = itemView.findViewById(R.id.item_history_tv_step)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_history2, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val holder = viewHolder as ViewHolder
        val item = itemList[i]
        try {
            holder.start.text = DateFormatUtil().getTimeColon("${item.start}")
            holder.end.text = DateFormatUtil().getTimeColon("${item.end}")
            holder.time.text = DateFormatUtil().getTimeHMS("${item.time}")
            holder.step.text = item.step

        } catch (e: Exception) {
//            e.printStackTrace()
            LogUtil.e(e)
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}