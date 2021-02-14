package com.picka.smombieapp.view.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.picka.smombieapp.R
import com.picka.smombieapp.base.BaseApplication
import com.picka.smombieapp.data.HistoryData
import com.picka.smombieapp.data.HistoryData2
import com.picka.smombieapp.utils.LogUtil
import java.text.SimpleDateFormat
import java.util.*


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
            val start1 = item.start?.split(" ")
            val start = start1?.get(1)?.split(":")
            val end1 = item.end?.split(" ")
            if (end1 != null && end1.size>1) {
                val end = end1[1].split(":")
                if (end.size>1)
                    holder.end.text = "${end[0]}:${end[1]}"
            } else {
                holder.end.text = "-"
            }
            val time = item.time?.split(":")
            if (time != null && time.size>1) {
                if (time.size == 2) {
                    holder.time.text = "${time[0]}분 ${time[1]}초"
                } else {
                    holder.time.text = "${time[0]}시간 ${time[1]}분 ${time[2]}초"
                }
            } else {
                holder.time.text = "-"
            }

            holder.start.text = "${start?.get(0)}:${start?.get(1)}"
            holder.step.text = item.step

        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
        }

    }


    override fun getItemCount(): Int {
        return itemList.size
    }
}