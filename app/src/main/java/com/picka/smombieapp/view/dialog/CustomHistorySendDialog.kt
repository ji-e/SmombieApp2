package com.picka.smombieapp.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.picka.smombieapp.databinding.DialogHistorySendBinding
import com.picka.smombieapp.utils.SizeUtil


class CustomHistorySendDialog(
    context: Context,
    customDialogClickListener: CustomHistorySendListener
) : Dialog(context),
    View.OnClickListener {
    private var mContext = context
    private var mCustomDialogClickListener = customDialogClickListener

    lateinit var binding: DialogHistorySendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogHistorySendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.dialogHistorySentBtnClose.setOnClickListener(this)
        binding.dialogHistorySendBtn.setOnClickListener(this)

        binding.dialogHistorySendEdt.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.dialogHistorySendBtn.isEnabled = binding.dialogHistorySendEdt.text.toString().isNotEmpty()
            }

        })
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window?.attributes)
        lp.width = SizeUtil(context).dp(280f)
        lp.height =SizeUtil(context).dp(350f)
        val window: Window? = window
        window?.attributes = lp
    }

    override fun onClick(v: View?) {
        dismiss()
        when (v) {
            binding.dialogHistorySentBtnClose -> dismiss()
            binding.dialogHistorySendBtn -> mCustomDialogClickListener.onSendEmail(binding.dialogHistorySendEdt.text.toString())
        }

    }

}