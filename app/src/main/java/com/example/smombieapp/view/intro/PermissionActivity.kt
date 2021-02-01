package com.example.smombieapp.view.intro

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.smombieapp.base.BaseActivity
import com.example.smombieapp.databinding.ActivityPermissionBinding
import com.example.smombieapp.utils.Constants
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck()
        }
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
        } else if (requestCode == Constants.CHECK_OVERLAY_PERMISSION) {
            permissionOnSuccess()
        }
    }

    /** 권한설정성공 */
    private fun permissionOnSuccess() {
        if (!Settings.canDrawOverlays(this)) {
            // 다른앱 위에 그리기 체크
//            setOverlayPermission()
        } else {
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
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } else {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACTIVITY_RECOGNITION
                    //            Manifest.permission.READ_EXTERNAL_STORAGE,
                    //            Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }

        for (i in permission.indices) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, permission[i])

            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                permissionList.add(permission[i])
            }
        }

        if (permissionList.isEmpty()) {
            permissionOnSuccess()
        }
    }

    /** 권한설정 */
    private fun setPermission() {
        val permissionArr = arrayOfNulls<String>(permissionList.size)
        for (i in 0 until permissionList.size) {
            permissionArr[i] = permissionList[i]
        }

        ActivityCompat.requestPermissions(
            this,
            permissionArr,
            Constants.CHECK_PERMISSION
        )
        if (!Settings.canDrawOverlays(this)) {
            setOverlayPermission()
        }
    }

    private fun setOverlayPermission() {
        val uri: Uri = Uri.fromParts("package", packageName, null)
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
        startActivityForResult(intent, Constants.CHECK_OVERLAY_PERMISSION)
    }
}