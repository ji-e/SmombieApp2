package com.picka.smombieapp.view.intro

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.picka.smombieapp.base.BaseActivity
import com.picka.smombieapp.base.BaseApplication
import com.picka.smombieapp.databinding.ActivityPermissionBinding
import com.picka.smombieapp.utils.Constants
import com.picka.smombieapp.view.MainActivity


/**
 * create by jieun
 */
class PermissionActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityPermissionBinding
    private var permissionList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        permissionCheck()

    }

    private fun initView() {
        binding.permissionBtnConfirm.setOnClickListener(this)
        binding.permissionBtnClose.setOnClickListener(this)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            binding.msg4.visibility = View.GONE
            binding.msg41.visibility = View.GONE
        }

    }

    override fun onClick(v: View?) {
        if (!onSingleClick()) return

        when (v) {
            binding.permissionBtnConfirm -> setPermission()
            binding.permissionBtnClose -> exitApp()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.CHECK_PERMISSION) {
            for (i in grantResults.indices) {
                // 알림 권한은 거절해도 잠금은 동작한다
                if (permissions[i] == Manifest.permission.POST_NOTIFICATIONS) continue
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    permissionOnFail()
                    return
                }
            }

            permissionOnSuccess()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.CHECK_OVERLAY_PERMISSION || requestCode == Constants.CHECK_IGNORE_BATTERY_OPTIMIZATION) {
            permissionOnSuccess()
        }
    }

    /** 권한설정성공 */
    private fun permissionOnSuccess() {
        val packageName = packageName
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager

        if (!Settings.canDrawOverlays(this)) {
            // 다른앱 위에 그리기 체크
            setOverlayPermission()
        } else if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            setIgnoreBattery()
        } else {
            if (BaseApplication.useSharedPreference.getPreferencesData(Constants.IS_PRIVACY) != "Y") {
                startActivity(Intent(this, PrivacyActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }
    }

    /** 권한설정실패 */
    private fun permissionOnFail() {
        exitApp()
    }


    /** 권한체크 */
    private fun permissionCheck() {
        // 기록 파일은 앱 전용 폴더에 쓰므로 Android 10+는 저장소 권한이 필요 없다 (13+는 요청해도 거절된다)
        val permission: Array<String> =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION)
            } else {
                arrayOf(
                    Manifest.permission.ACTIVITY_RECOGNITION,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }

        for (i in permission.indices) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, permission[i])

            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                permissionList.add(permission[i])
            }
        }


        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!Settings.canDrawOverlays(this) || !pm.isIgnoringBatteryOptimizations(packageName)) {
            return
        }

        if (permissionList.isEmpty()) {
            permissionOnSuccess()
        }
    }

    /** 권한설정 */
    private fun setPermission() {
        if (permissionList.isNotEmpty()) {
            val permissionArr = arrayOfNulls<String>(permissionList.size)
            for (i in 0 until permissionList.size) {
                permissionArr[i] = permissionList[i]
            }

            ActivityCompat.requestPermissions(
                this,
                permissionArr,
                Constants.CHECK_PERMISSION
            )
        } else {
            permissionOnSuccess()
        }
    }

    private fun setOverlayPermission() {
        val uri: Uri = Uri.fromParts("package", packageName, null)
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
        startActivityForResult(intent, Constants.CHECK_OVERLAY_PERMISSION)
    }

    private fun setIgnoreBattery() {
        val packageName = packageName
        val intent = Intent().apply {
            action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            data = Uri.parse("package:$packageName")
        }

        startActivityForResult(intent, Constants.CHECK_IGNORE_BATTERY_OPTIMIZATION)
    }
}