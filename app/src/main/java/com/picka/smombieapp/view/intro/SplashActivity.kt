package com.picka.smombieapp.view.intro

import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.picka.smombieapp.R
import com.picka.smombieapp.base.BaseActivity
import com.picka.smombieapp.databinding.ActivityPermissionBinding
import com.picka.smombieapp.databinding.ActivitySplashBinding

/**
 * create by jieun
 */
class SplashActivity : BaseActivity(){
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed(
            {
                startActivity(Intent(this@SplashActivity, PermissionActivity::class.java))
                finish()
            }
        , 2000)
    }
}