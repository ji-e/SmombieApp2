package com.example.smombieapp.view.dialog

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.smombieapp.R
import com.example.smombieapp.databinding.DialogBottomBasicBinding

/**
 * 다이얼로그
 * context: Context
 * theme: Int
 */
class CustomDialog(mContext: Context) : BaseBottomDialog(mContext), View.OnClickListener {


    private var binding: DialogBottomBasicBinding = DialogBottomBasicBinding.inflate(layoutInflater)

    private lateinit var tvTitle: TextView
    private lateinit var tvContent: TextView
    private lateinit var btnClose: ImageButton
    private lateinit var btnNo: Button
    private lateinit var btnYes: Button

    private var mCloseBtnClickListener: View.OnClickListener? = null
    private var mNoBtnClickListener: View.OnClickListener? = null
    private var mYesBtnClickListener: View.OnClickListener? = null

    init {
        setContentView(binding.root)
        initView()
    }

    fun initView() {
        tvTitle = binding.dialogTvTitle
        tvContent = binding.dialogTvContent
        btnClose = binding.dialogBtnClose
        btnNo = binding.dialogBtnNo
        btnYes = binding.dialogBtnYes

        btnClose.setOnClickListener(this)
        btnNo.setOnClickListener(this)
        btnYes.setOnClickListener(this)
    }


    fun setBottomDialog(strContent: String,
                        strYes: String, onYesListener: View.OnClickListener?) {

        setBottomDialog(mContext.getString(R.string.dialog_title), strContent, null, null, null, strYes, onYesListener)
    }


    fun setBottomDialog(strTitle: String,
                        strContent: String,
                        onCloseListener: View.OnClickListener?,
                        strYes: String, onYesListener: View.OnClickListener?) {

        setBottomDialog(strTitle, strContent, onCloseListener, null, null, strYes, onYesListener)
    }

    fun setBottomDialog(strTitle: String,
                        strContent: String,
                        onCloseListener: View.OnClickListener?,
                        strNo: String?, onNoListener: View.OnClickListener?,
                        strYes: String, onYesListener: View.OnClickListener?) {

        onCloseListener?.let { btnClose.setOnClickListener(it) }
        onNoListener?.let { btnNo.setOnClickListener(it) }
        onYesListener?.let { btnYes.setOnClickListener(it) }


        if (onCloseListener == null) {
            btnClose.visibility = View.GONE
            setCancelable(false)
        }

        tvTitle.text = strTitle
        tvContent.text = strContent
        btnNo.text = strNo
        btnYes.text = strYes

        if (strNo.isNullOrEmpty()) {
            binding.dialogBtnNo.visibility = View.GONE
        }


        // 다이얼로그 높이 설정
        setPeekHeight(250f)
    }

    override fun onClick(view: View) {
        when (view) {
            btnClose, btnNo, btnYes -> {
                dismiss()
            }
        }
    }
}