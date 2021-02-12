package com.picka.smombieapp.utils

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast


/**
 * create by jieun
 */
class CustomToast(private val mContext: Context) {

    fun makeToast(msg:String):Toast{
        val toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT)
        val group = toast.view as ViewGroup
        val messageTextView = group.getChildAt(0) as TextView
        messageTextView.run {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
            gravity = Gravity.CENTER
        }

        return toast
    }

    fun showToast(msg: String) {
        val toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT)
        val group = toast.view as ViewGroup
        val messageTextView = group.getChildAt(0) as TextView
        messageTextView.run {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
            gravity = Gravity.CENTER
        }
        toast.show()
    }
}