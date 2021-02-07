package com.example.smombieapp.view.intro

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.smombieapp.base.BaseActivity
import com.example.smombieapp.databinding.ActivityPermissionBinding
import com.example.smombieapp.utils.Constants
import com.example.smombieapp.utils.LogUtil
import com.example.smombieapp.view.MainActivity


/**
 * create by jieun
 */
class PermissionActivity : BaseActivity() {
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
        binding.permissionBtnConfirm.setOnClickListener { setPermission() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.CHECK_PERMISSION) {
            for (i in grantResults.indices) {
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
        } else  if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            setIgnoreBattery()
        }else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    /** 권한설정실패 */
    private fun permissionOnFail() {
        exitApp()
    }


    /** 권한체크 */
    private fun permissionCheck() {
        val permission: Array<String> =
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE
                    )
                } else {
                    arrayOf(
                            Manifest.permission.ACTIVITY_RECOGNITION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE
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
        if(permissionList.isNotEmpty()) {

            val permissionArr = arrayOfNulls<String>(permissionList.size)
            for (i in 0 until permissionList.size) {
                permissionArr[i] = permissionList[i]
            }

            ActivityCompat.requestPermissions(
                    this,
                    permissionArr,
                    Constants.CHECK_PERMISSION
            )
        }else {
            permissionOnSuccess()
        }
//        if (!Settings.canDrawOverlays(this)) {
//            setOverlayPermission()
//        }
    }

    private fun setOverlayPermission() {
        val uri: Uri = Uri.fromParts("package", packageName, null)
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
        startActivityForResult(intent, Constants.CHECK_OVERLAY_PERMISSION)
    }

    private fun setIgnoreBattery() {
        val intent = Intent()

        val packageName = packageName
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager

//        if (pm.isIgnoringBatteryOptimizations(packageName)) {
////            intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
//        } else {
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
//        }
        startActivityForResult(intent, Constants.CHECK_IGNORE_BATTERY_OPTIMIZATION)
    }
}