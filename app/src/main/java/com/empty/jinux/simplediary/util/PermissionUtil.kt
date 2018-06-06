package com.empty.jinux.simplediary.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.empty.jinux.simplediary.R
import com.google.common.collect.Lists

object PermissionUtil {

    private fun needCheckPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    /**
     * 获取sd存储卡读写权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    fun getExternalStoragePermissions(context: Activity, requestCode: Int): Boolean {
        return requestPermissions(context, requestCode, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    /**
     * 获取拍照权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    fun getCameraPermissions(context: Activity, requestCode: Int): Boolean {
        return requestPermissions(context, requestCode, Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    /**
     * 获取麦克风权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    fun getAudioPermissions(context: Activity, requestCode: Int): Boolean {
        return requestPermissions(context, requestCode, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    /**
     * 获取定位权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    fun getLocationPermissions(context: Activity, requestCode: Int): Boolean {
        return requestPermissions(context, requestCode,
                Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /**
     * 获取读取联系人权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    fun getContactsPermissions(context: Activity, requestCode: Int): Boolean {
        return requestPermissions(context, requestCode,
                Manifest.permission.READ_CONTACTS)
    }

    /**
     * 获取发送短信权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    fun getSendSMSPermissions(context: Activity, requestCode: Int): Boolean {
        return requestPermissions(context, requestCode,
                Manifest.permission.SEND_SMS)
    }

    /**
     * 获取拨打电话权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    fun getCallPhonePermissions(context: Activity, requestCode: Int): Boolean {
        return requestPermissions(context, requestCode,
                Manifest.permission.CALL_PHONE)
    }


    private fun getDeniedPermissions(context: Activity, vararg permissions: String): List<String> {
        if (!needCheckPermission()) {
            return Lists.newArrayList()
        }
        return permissions.filter { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED }
    }

    /**
     * 是否拥有权限
     */
    private fun hasAllPermissions(context: Activity, vararg permissions: String): Boolean {
        if (!needCheckPermission()) {
            return true
        }
        return permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
    }

    /**
     * 是否拒绝了再次申请权限的请求（点击了不再询问）
     */
    private fun needGotoPermissionSettings(context: Activity, vararg permissions: String): Boolean {
        if (!needCheckPermission()) {
            return false
        }
        val deniedPermissions = getDeniedPermissions(context, *permissions)

        //当用户之前已经请求过该权限并且拒绝了授权这个方法返回true
        return deniedPermissions.any { !ActivityCompat.shouldShowRequestPermissionRationale(context, it) }
    }

    /**
     * 打开app详细设置界面<br></br>
     *
     *
     * 在 onActivityResult() 中没有必要对 resultCode 进行判断，因为用户只能通过返回键才能回到我们的 App 中，<br></br>
     * 所以 resultCode 总是为 RESULT_CANCEL，所以不能根据返回码进行判断。<br></br>
     * 在 onActivityResult() 中还需要对权限进行判断，因为用户有可能没有授权就返回了！<br></br>
     */
    private fun startPermissionSettings(context: Activity, requestCode: Int) {
        Toast.makeText(context, context.getString(R.string.guide_open_permission), Toast.LENGTH_LONG).show()

        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivityForResult(intent, requestCode)


    }

    /**
     * 申请权限<br></br>
     * 使用onRequestPermissionsResult方法，实现回调结果或者自己普通处理
     *
     * @return 是否已经获取权限
     */
    private fun requestPermissions(context: Activity, requestCode: Int, vararg permissions: String): Boolean {

        if (hasAllPermissions(context, *permissions)) {
            return true
        }

        if (needGotoPermissionSettings(context, *permissions)) {
            startPermissionSettings(context, requestCode)
            //返回结果onActivityResult
        } else {
            val deniedPermissions = getDeniedPermissions(context, *permissions)
            ActivityCompat.requestPermissions(context, deniedPermissions.toTypedArray(), requestCode)
        }
        return false
    }

    /**
     * 申请权限返回方法
     */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                   grantResults: IntArray, callBack: OnRequestPermissionsResultCallbacks?) {
        // Make a collection of granted and denied permissions from the request.
        val granted = mutableListOf<String>()
        val denied = mutableListOf<String>()
        for (i in permissions.indices) {
            val perm = permissions[i]
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm)
            } else {
                denied.add(perm)
            }
        }

        if (null != callBack) {
            if (!granted.isEmpty()) {
                callBack.onPermissionsGranted(requestCode, granted, denied.isEmpty())
            }
            if (!denied.isEmpty()) {
                callBack.onPermissionsDenied(requestCode, denied, granted.isEmpty())
            }
        }


    }


    /**
     * 申请权限返回
     */
    interface OnRequestPermissionsResultCallbacks {

        /**
         * @param isAllGranted 是否全部同意
         */
        fun onPermissionsGranted(requestCode: Int, perms: List<String>, isAllGranted: Boolean)

        /**
         * @param isAllDenied 是否全部拒绝
         */
        fun onPermissionsDenied(requestCode: Int, perms: List<String>, isAllDenied: Boolean)

    }
}