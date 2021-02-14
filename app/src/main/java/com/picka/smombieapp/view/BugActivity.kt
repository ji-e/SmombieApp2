package com.picka.smombieapp.view

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.picka.smombieapp.R
import com.picka.smombieapp.databinding.ActivityBugBinding
import com.picka.smombieapp.utils.Constants
import com.picka.smombieapp.utils.EmailUtil
import com.picka.smombieapp.view.intro.PrivacyActivity

class BugActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityBugBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBugBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        binding.bugBtnBack.setOnClickListener(this)
        binding.bugBtnReport.setOnClickListener(this)
        binding.bugTvEmail.setOnClickListener(this)
        binding.butTvPrivacy.setOnClickListener(this)
    }

    private fun copyEmail() {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("email", getString(R.string.bug_email))
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(this, getString(R.string.bug_toast), Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.bugBtnBack -> onBackPressed()
            binding.bugTvEmail -> copyEmail()
            binding.butTvPrivacy -> {
                val intent = Intent(this, PrivacyActivity::class.java).apply {
                    action = "setting"
                }
                startActivity(intent)
            }
            binding.bugBtnReport -> {
                val address = arrayOf(Constants.BUG_EMAIL)
                EmailUtil().sendEmailToAdmin(this, getString(R.string.btn_bug), address)
            }
        }
    }
}