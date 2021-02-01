package com.example.smombieapp.view.intro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.smombieapp.R
import com.example.smombieapp.base.BaseActivity

/**
 * create by jieun
 */
class SplashActivity : BaseActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed(
            {
                // todo 스플래시
                startActivity(Intent(this@SplashActivity, PermissionActivity::class.java))
                finish()
            }
        , 2000)
    }
}