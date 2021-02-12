package com.picka.smombieapp.view.log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.picka.smombieapp.R
import com.picka.smombieapp.utils.LogUtil
import org.json.JSONArray


class LogAdapter(private val itemList: JSONArray) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    fun RecyclerAdapter() {
//        itemList.clear()
//        for (i in 0..9) {
//            itemList.add(i.toString() + "번째 아이템")
//        }
//        notifyDataSetChanged()
//    }

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val start: TextView = itemView.findViewById(R.id.log_item_start)
        val end: TextView = itemView.findViewById(R.id.log_item_end)
        val time: TextView = itemView.findViewById(R.id.log_item_time)
        val step: TextView = itemView.findViewById(R.id.log_item_step)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_log, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val holder = viewHolder as ViewHolder
        val item = itemList.getJSONObject(i)
        try {
            holder.start.text = item.getString("start")
            holder.end.text = item.getString("end")
            holder.time.text = item.getString("time")
            holder.step.text = item.getString("step")

        } catch (e: Exception) {
            LogUtil.e(e)
        }

    }


    override fun getItemCount(): Int {
        return itemList.length()
    }
}