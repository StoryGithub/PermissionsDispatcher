package permissions.dispatcher.ktx

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import permissions.dispatcher.PermissionRequest

internal typealias Func = () -> Unit
internal typealias ShowRationaleFunc = (PermissionRequest) -> Unit

fun AppCompatActivity.withPermissionsCheck(vararg permissions: String,
                                           showRationale: ShowRationaleFunc? = null,
                                           permissionDenied: Func? = null,
                                           neverAskAgain: Func? = null,
                                           needsPermission: Func) {
    if (permissions.size == 1 && (permissions.first() == Manifest.permission.WRITE_SETTINGS ||
            permissions.first() == Manifest.permission.SYSTEM_ALERT_WINDOW)) {
        invokeSpecial(permissions, this, showRationale, permissionDenied, neverAskAgain, needsPermission)
    } else {
        invoke(permissions, this, showRationale, permissionDenied, neverAskAgain, needsPermission)
    }
}

fun Fragment.withPermissionsCheck(vararg permissions: String,
                                  permissionDenied: Func? = null,
                                  showRationale: ShowRationaleFunc? = null,
                                  neverAskAgain: Func? = null,
                                  needsPermission: Func) {
    if (permissions.size == 1 && (permissions.first() == Manifest.permission.WRITE_SETTINGS ||
            permissions.first() == Manifest.permission.SYSTEM_ALERT_WINDOW)) {
        invokeSpecial(permissions, this, showRationale, permissionDenied, neverAskAgain, needsPermission)
    } else {
        invoke(permissions, this, showRationale, permissionDenied, neverAskAgain, needsPermission)
    }
}
