package com.example.smombieapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.smombieapp.BuildConfig
import com.example.smombieapp.R
import com.example.smombieapp.base.BaseActivity
import com.example.smombieapp.base.BaseApplication
import com.example.smombieapp.databinding.ActivityMainBinding
import com.example.smombieapp.databinding.ActivitySettingBinding
import com.example.smombieapp.utils.Constants
import com.example.smombieapp.utils.LogUtil
import com.example.smombieapp.view.dialog.CustomDialog
import com.example.smombieapp.view.intro.PrivacyActivity

class SettingActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivitySettingBinding

    private var isEmailView = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Thread.setDefaultUncaughtExceptionHandler(UncaughtException())

        init()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.settingImgBack -> onBackPressed()
            binding.settingPrivacy -> {
                val intent = Intent(this, PrivacyActivity::class.java).apply {
                    action = "setting"
                }
                startActivity(intent)
            }
            binding.settingEmail -> showEmailView()
            binding.settingBtnEmail -> {
                val email = binding.settingEtEmail.text.toString()
                BaseApplication.useSharedPreference.setPreferencesData(Constants.EXCEL_EMAIL, email)
                showEmailSaveDialog()
            }
        }

    }

    private fun init() {
        binding.settingImgBack.setOnClickListener(this)
        binding.settingPrivacy.setOnClickListener(this)
        binding.settingEmail.setOnClickListener(this)
        binding.settingBtnEmail.setOnClickListener(this)

        val email =   BaseApplication.useSharedPreference.getPreferencesData(Constants.EXCEL_EMAIL)
        binding.settingEtEmail.setText(email)

        binding.settingTvVersion.text = "${BuildConfig.VERSION_NAME}"
    }

    /** email 입력 레이아웃 */
    private fun showEmailView() {
        isEmailView = !isEmailView
        if (isEmailView) {
            binding.settingImgEmailArr.setBackgroundResource(R.drawable.btn_list_arrow3)
            binding.settingEmailEd.visibility = View.VISIBLE
        } else {
            binding.settingImgEmailArr.setBackgroundResource(R.drawable.btn_list_arrow2)
            binding.settingEmailEd.visibility = View.GONE
        }
    }

    /** email 저장 팝업 */
    private fun showEmailSaveDialog() {
        val customDialog = CustomDialog(this).apply {
            setBottomDialog(
                getString(R.string.dialog_title),
                getString(R.string.dialog_email_content),
                null,
                getString(R.string.btn_confirm),
                View.OnClickListener {
                    dismiss()
                }

            )
        }
        customDialog.show()
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }

    inner class UncaughtException : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            LogUtil.e(t, e)
        }
    }


}