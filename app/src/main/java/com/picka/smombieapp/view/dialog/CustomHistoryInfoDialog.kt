package com.picka.smombieapp.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.picka.smombieapp.databinding.DialogHistoryInfoBinding
import com.picka.smombieapp.utils.SizeUtil


class CustomHistoryInfoDialog(
    context: Context
) : Dialog(context) {
    private var mContext = context

    lateinit var binding: DialogHistoryInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogHistoryInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.dialogHistoryInfoBtnClose.setOnClickListener { dismiss() }
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window?.attributes)
        lp.width = SizeUtil(context).dp(280f)
        lp.height = SizeUtil(context).dp(350f)
        val window: Window? = window
        window?.attributes = lp
    }


}