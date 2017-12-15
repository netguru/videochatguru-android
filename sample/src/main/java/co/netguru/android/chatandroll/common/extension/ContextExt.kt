package co.netguru.android.chatandroll.common.extension

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat

fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

fun Context.startAppSettings() {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).run {
        addCategory(Intent.CATEGORY_DEFAULT)
        data = Uri.parse("package:" + packageName)
        startActivity(this)
    }
}

fun Context.checkIsPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.areAllPermissionsGranted(vararg permission: String) = permission.all { checkIsPermissionGranted(it) }