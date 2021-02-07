package com.example.smombieapp.view

import android.app.Dialog

import android.content.Context
import com.example.smombieapp.R
import com.example.smombieapp.utils.LogUtil


class CustomLoading{
        companion object {
            private var mLoadingBar: Dialog? = null
            private fun createCustomLoadingBarDialog(mContext: Context): Dialog {
                mLoadingBar = Dialog(mContext, R.style.Loading).apply {
                    setCancelable(false)
                    setContentView(R.layout.loading)
                }

                return mLoadingBar as Dialog
            }

            fun showLoadingBar(mContext: Context) {
                createCustomLoadingBarDialog(mContext)
                mLoadingBar?.show()
            }

            fun hideLoadingBar() {
                if (mLoadingBar != null) {
                    try {
                        mLoadingBar?.dismiss()
                        mLoadingBar = null
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }


    }