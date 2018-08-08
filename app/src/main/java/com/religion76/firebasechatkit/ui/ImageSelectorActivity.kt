package com.religion76.firebasechatkit.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.sw926.imagefileselector.ErrorResult
import com.sw926.imagefileselector.ImageFileSelector

/**
 * Created by SunChao on 2018/8/8.
 */
@SuppressLint("Registered")
open class ImageSelectorActivity : AppCompatActivity() {

    private val REQUEST_SELECT_IMAGE = 0x12
    private val PERMISSIONS_REQUEST_CODE = 0x13

    private val imageFileSelector by lazy {
        val selector = ImageFileSelector(this)
        selector.setQuality(80)
        selector
    }

    @SuppressLint("NewApi")
    protected fun selectImage(onSucceed: ((String?) -> Unit)) {
        imageFileSelector.setCallback(object :ImageFileSelector.Callback{
            override fun onSuccess(file: String?) {
                onSucceed.invoke(file)
            }

            override fun onError(errorResult: ErrorResult?) {

            }
        })

        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        val list = mutableListOf<String>()
        permissions.map {
            if (!isHavePermission(this, it)) {
                list.add(it)
            }
        }
        if (list.isEmpty()) {
            imageFileSelector.selectImage(this, REQUEST_SELECT_IMAGE)
        } else {
            requestPermissions(list.toTypedArray(), PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun isHavePermission(context: Context, permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imageFileSelector.onActivityResult(this, requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        imageFileSelector.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        imageFileSelector.onRestoreInstanceState(savedInstanceState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        imageFileSelector.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            grantResults.map {
                if (it != PackageManager.PERMISSION_GRANTED) {
                    return
                }
            }
            imageFileSelector.selectImage(this, REQUEST_SELECT_IMAGE)
        }
    }
}