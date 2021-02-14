package com.picka.smombieapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.picka.smombieapp.BuildConfig
import com.picka.smombieapp.R
import com.picka.smombieapp.databinding.ActivityMainBinding
import com.picka.smombieapp.databinding.ActivityVersionBinding

class VersionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVersionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVersionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.versionTv.text =  getString(R.string.version_msg) + " ${BuildConfig.VERSION_NAME}"
        binding.versionBtnBack.setOnClickListener {
            onBackPressed()
        }

    }
}