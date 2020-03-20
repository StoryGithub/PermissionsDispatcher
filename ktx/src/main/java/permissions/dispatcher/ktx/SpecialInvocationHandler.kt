package permissions.dispatcher.ktx

import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import permissions.dispatcher.PermissionUtils

internal fun invokeSpecial(permissions: Array<out String>,
                           target: Any,
                           showRationale: ShowRationaleFunc? = null,
                           permissionDenied: Func? = null,
                           neverAskAgain: Func? = null,
                           needsPermission: Func) {
    val activity = when (target) {
        is AppCompatActivity -> target
        is Fragment -> target.activity
        else -> null
    } ?: return
    if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(activity)) {
        needsPermission()
    } else {
        if (PermissionUtils.shouldShowRequestPermissionRationale(activity, *permissions)) {
            val request = KtxPermissionRequest.create(permissionDenied) {
                requestOverlayPermission(
                    activity,
                    permissions,
                    needsPermission,
                    neverAskAgain,
                    permissionDenied
                )
            }
            showRationale?.invoke(request)
        } else {
            requestOverlayPermission(
                activity,
                permissions,
                needsPermission,
                neverAskAgain,
                permissionDenied)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.M)
internal fun requestOverlayPermission(target: Any,
                                      permissions: Array<out String>,
                                      needsPermission: Func,
                                      neverAskAgain: Func?,
                                      onPermissionDenied: Func?) {
    val fragment = when (target) {
        is AppCompatActivity -> target.supportFragmentManager.findFragmentByTag(
            PermissionsRequestFragment.tag) as? PermissionsRequestFragment
        is Fragment -> target.childFragmentManager.findFragmentByTag(
            PermissionsRequestFragment.tag) as? PermissionsRequestFragment
        else -> null
    }
    if (fragment != null) {
        fragment.requestOverlayPermission(permissions, needsPermission, neverAskAgain, onPermissionDenied)
    } else {
        val newFragment = PermissionsRequestFragment.newInstance()
        when (target) {
            is AppCompatActivity ->
                target.supportFragmentManager
                    .beginTransaction().add(newFragment, PermissionsRequestFragment.tag).commitNow()
            is Fragment ->
                target.childFragmentManager
                    .beginTransaction().add(newFragment, PermissionsRequestFragment.tag).commitNow()
        }
    }
}
