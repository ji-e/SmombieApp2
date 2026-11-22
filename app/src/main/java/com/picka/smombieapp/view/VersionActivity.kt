package com.picka.smombieapp.view

import android.os.Bundle
import com.picka.smombieapp.BuildConfig
import com.picka.smombieapp.R
import com.picka.smombieapp.base.BaseActivity
import com.picka.smombieapp.databinding.ActivityVersionBinding
import com.picka.smombieapp.utils.LogUtil
import org.jsoup.Jsoup
import java.io.IOException

class VersionActivity : BaseActivity() {
    private lateinit var binding: ActivityVersionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVersionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        getVersion()
    }

    private fun initView(){
        binding.versionTvVer.text = getString(R.string.version_current) + " ${BuildConfig.VERSION_NAME}"
        binding.versionBtnBack.setOnClickListener {
            if (onSingleClick()) {
                onBackPressed()
            }
        }
    }

    /** 버전체크 */
    private fun getVersion() {
        val thisVer = (BuildConfig.VERSION_NAME).replace(".", "")
        var marketVer = "0"
        Thread {
            try {
                val doc = Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.picka.smombieapp").get()
                val ver = doc.select(".htlgb").eq(6)
                for (mElement in ver) {
                    runOnUiThread {
                        marketVer = (mElement.text().trim()).replace(".", "")
                        val strVer =
                            if (thisVer.toInt() < marketVer.toInt()) {
                                getString(R.string.version_msg2)
                            } else {
                                getString(R.string.version_msg)
                            }
                        binding.versionTvMsg.text = strVer
                    }
                }
            } catch (e: Exception) {
                // 플레이스토어 페이지 구조가 바뀌면 버전 비교만 건너뛴다
                LogUtil.e(e)
            }
        }.start()

    }
}