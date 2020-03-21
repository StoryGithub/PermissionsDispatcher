package permissions.dispatcher.ktx

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
    PermissionRequestType.create(permissions).invoke(
        permissions,
        this,
        showRationale,
        permissionDenied,
        neverAskAgain,
        needsPermission)
}

fun Fragment.withPermissionsCheck(vararg permissions: String,
                                  permissionDenied: Func? = null,
                                  showRationale: ShowRationaleFunc? = null,
                                  neverAskAgain: Func? = null,
                                  needsPermission: Func) {
    PermissionRequestType.create(permissions).invoke(
        permissions,
        this,
        showRationale,
        permissionDenied,
        neverAskAgain,
        needsPermission)
}
