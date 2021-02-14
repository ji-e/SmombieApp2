package com.picka.smombieapp.view

import android.os.Bundle
import com.picka.smombieapp.BuildConfig
import com.picka.smombieapp.R
import com.picka.smombieapp.base.BaseActivity
import com.picka.smombieapp.databinding.ActivityVersionBinding

class VersionActivity : BaseActivity() {
    private lateinit var binding: ActivityVersionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVersionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.versionTv.text = getString(R.string.version_msg) + " ${BuildConfig.VERSION_NAME}"
        binding.versionBtnBack.setOnClickListener {
            if (onSingleClick()) {
                onBackPressed()
            }
        }

    }
}