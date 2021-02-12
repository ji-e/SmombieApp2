package com.picka.smombieapp.utils

import android.content.Context
import android.util.TypedValue

class SizeUtil(private val mContext: Context) {

    private val DISPLAY_METRICS = mContext.resources.displayMetrics

    fun dp(dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, DISPLAY_METRICS).toInt()
    }

    fun sp(sp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, DISPLAY_METRICS).toInt()
    }

    fun px(px: Float): Int {
        return px.toInt() / (DISPLAY_METRICS.densityDpi / 160f).toInt()
    }
}