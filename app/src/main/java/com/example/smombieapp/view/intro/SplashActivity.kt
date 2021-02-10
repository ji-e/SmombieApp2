package com.example.smombieapp.view.intro

import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.smombieapp.R
import com.example.smombieapp.base.BaseActivity
import com.example.smombieapp.databinding.ActivityPermissionBinding
import com.example.smombieapp.databinding.ActivitySplashBinding

/**
 * create by jieun
 */
class SplashActivity : BaseActivity(){
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logo.background = ShapeDrawable(OvalShape())
        binding.logo.clipToOutline = true

        Handler(Looper.getMainLooper()).postDelayed(
            {
                // todo 스플래시
                startActivity(Intent(this@SplashActivity, PermissionActivity::class.java))
                finish()
            }
        , 2000)
    }
}