package com.example.smombieapp.view.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import com.example.smombieapp.base.BaseActivity
import com.example.smombieapp.base.BaseApplication
import com.example.smombieapp.databinding.ActivityPrivacyBinding
import com.example.smombieapp.utils.Constants
import com.example.smombieapp.utils.LogUtil
import com.example.smombieapp.view.MainActivity

class PrivacyActivity : BaseActivity() {
    lateinit var binding: ActivityPrivacyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Thread.setDefaultUncaughtExceptionHandler(UncaughtException())

        initView()

    }

    private fun initView() {
        binding.privacyBtnAgree.visibility =
            if (intent.action == "setting") View.GONE
            else View.VISIBLE

        val mWebView = binding.privacyWebview

        //웹뷰의 설정을 다음과 같이 맞춰주시기 바랍니다.
        mWebView.settings.javaScriptEnabled = true //필수설정(true)
        mWebView.settings.domStorageEnabled = true //필수설정(true)
        mWebView.settings.javaScriptCanOpenWindowsAutomatically = true //필수설정(true)


        mWebView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        mWebView.settings.loadsImagesAutomatically = true
        mWebView.settings.builtInZoomControls = true
        mWebView.settings.setSupportZoom(true)
        mWebView.settings.setSupportMultipleWindows(true)
        mWebView.settings.loadWithOverviewMode = true
        mWebView.settings.useWideViewPort = true
        mWebView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

        mWebView.loadUrl(Constants.PRIVACY_URL)

        binding.privacyBtnAgree.setOnClickListener {
            BaseApplication.useSharedPreference.setPreferencesData(Constants.IS_PRIVACY, "Y")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.privacyBtnClose.setOnClickListener {
            onBackPressed()
        }
    }


    inner class UncaughtException : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            LogUtil.e(t, e)
        }
    }
}