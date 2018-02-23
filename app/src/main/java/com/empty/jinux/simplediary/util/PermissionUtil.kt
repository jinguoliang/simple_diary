package com.empty.jinux.simplediary.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast

object PermissionUtil {
    /**
     * 是否需要检查权限
     */
    private fun needCheckPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    /**
     * 获取sd存储卡读写权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    fun getExternalStoragePermissions(@NonNull activity: Activity, requestCode: Int): Boolean {
        return requestPerssions(activity, requestCode, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    /**
     * 获取拍照权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    fun getCameraPermissions(@NonNull activity: Activity, requestCode: Int): Boolean {
        return requestPerssions(activity, requestCode, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    /**
     * 获取麦克风权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    fun getAudioPermissions(@NonNull activity: Activity, requestCode: Int): Boolean {
        return requestPerssions(activity, requestCode, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    /**
     * 获取定位权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    fun getLocationPermissions(@NonNull activity: Activity, requestCode: Int): Boolean {
        return requestPerssions(activity, requestCode, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    /**
     * 获取读取联系人权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    fun getContactsPermissions(@NonNull activity: Activity, requestCode: Int): Boolean {
        return requestPerssions(activity, requestCode, Manifest.permission.READ_CONTACTS)
    }

    /**
     * 获取发送短信权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    fun getSendSMSPermissions(@NonNull activity: Activity, requestCode: Int): Boolean {
        return requestPerssions(activity, requestCode, Manifest.permission.SEND_SMS)
    }

    /**
     * 获取拨打电话权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    fun getCallPhonePermissions(@NonNull activity: Activity, requestCode: Int): Boolean {
        return requestPerssions(activity, requestCode, Manifest.permission.CALL_PHONE)
    }


    fun getDeniedPermissions(@NonNull activity: Activity, @NonNull vararg permissions: String): List<String>? {
        if (!needCheckPermission()) {
            return null
        }
        val deniedPermissions = mutableListOf<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission)
            }
        }
        return if (!deniedPermissions.isEmpty()) {
            deniedPermissions
        } else null

    }

    /**
     * 是否拥有权限
     */
    fun hasPermissons(@NonNull activity: Activity, @NonNull vararg permissions: String): Boolean {
        if (!needCheckPermission()) {
            return true
        }
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /**
     * 是否拒绝了再次申请权限的请求（点击了不再询问）
     */
    fun deniedRequestPermissonsAgain(@NonNull activity: Activity, @NonNull vararg permissions: String): Boolean {
        if (!needCheckPermission()) {
            return false
        }
        val deniedPermissions = getDeniedPermissions(activity, *permissions)
        for (permission in deniedPermissions!!) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_DENIED) {

                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    //当用户之前已经请求过该权限并且拒绝了授权这个方法返回true
                    return true
                }
            }
        }

        return false
    }

    /**
     * 打开app详细设置界面<br></br>
     *
     *
     * 在 onActivityResult() 中没有必要对 resultCode 进行判断，因为用户只能通过返回键才能回到我们的 App 中，<br></br>
     * 所以 resultCode 总是为 RESULT_CANCEL，所以不能根据返回码进行判断。<br></br>
     * 在 onActivityResult() 中还需要对权限进行判断，因为用户有可能没有授权就返回了！<br></br>
     */
    fun startApplicationDetailsSettings(@NonNull activity: Activity, requestCode: Int) {
        Toast.makeText(activity, "点击权限，并打开全部权限", Toast.LENGTH_SHORT).show()

        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity.getPackageName(), null)
        intent.setData(uri)
        activity.startActivityForResult(intent, requestCode)


    }

    /**
     * 申请权限<br></br>
     * 使用onRequestPermissionsResult方法，实现回调结果或者自己普通处理
     *
     * @return 是否已经获取权限
     */
    fun requestPerssions(activity: Activity, requestCode: Int, vararg permissions: String): Boolean {

        if (!needCheckPermission()) {
            return true
        }

        if (!hasPermissons(activity, *permissions)) {
            if (deniedRequestPermissonsAgain(activity, *permissions)) {
                startApplicationDetailsSettings(activity, requestCode)
                //返回结果onActivityResult
            } else {
                val deniedPermissions = getDeniedPermissions(activity, *permissions)
                if (deniedPermissions != null) {
                    ActivityCompat.requestPermissions(activity, deniedPermissions.toTypedArray(), requestCode)
                    //返回结果onRequestPermissionsResult
                }
            }
            return false
        }
        return true
    }

    /**
     * 申请权限返回方法
     */
    fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>,
                                   @NonNull grantResults: IntArray, @NonNull callBack: OnRequestPermissionsResultCallbacks?) {
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
    //    public interface OnRequestPermissionsResultCallbacks extends ActivityCompat.OnRequestPermissionsResultCallback {
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