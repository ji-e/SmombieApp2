package com.picka.smombieapp.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.picka.smombieapp.databinding.DialogServiceEndBinding
import com.picka.smombieapp.utils.SizeUtil


class CustomServiceEndDialog(
    context: Context,
    customDialogClickListener: CustomServiceEndListener
) : Dialog(context),
    View.OnClickListener {
    private var mContext = context
    private var mCustomDialogClickListener = customDialogClickListener

    lateinit var binding: DialogServiceEndBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogServiceEndBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        binding.dialogServiceEndBtnClose.setOnClickListener(this)
        binding.dialogServiceEndBtnExit.setOnClickListener(this)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window?.attributes)
        lp.width = SizeUtil(context).dp(280f)
        lp.height = SizeUtil(context).dp(350f)
        val window: Window? = window
        window?.attributes = lp
    }

    override fun onClick(v: View?) {
        dismiss()
        when (v) {
            binding.dialogServiceEndBtnClose -> mCustomDialogClickListener.onCloseClick()
            binding.dialogServiceEndBtnExit -> mCustomDialogClickListener.onServiceEndClick()
        }

    }

}