package permissions.dispatcher.ktx

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.withPermissionsCheck(vararg permissions: String,
                                  onShowRationale: ShowRationaleFunc? = null,
                                  onPermissionDenied: Func? = null,
                                  onNeverAskAgain: Func? = null,
                                  requiresPermission: Func) {
    PermissionRequestType.from(permissions).invoke(
        permissions = permissions,
        activity = this,
        onShowRationale = onShowRationale,
        onPermissionDenied = onPermissionDenied,
        onNeverAskAgain = onNeverAskAgain,
        requiresPermission = requiresPermission)
}

fun Fragment.withPermissionsCheck(vararg permissions: String,
                                  onShowRationale: ShowRationaleFunc? = null,
                                  onPermissionDenied: Func? = null,
                                  onNeverAskAgain: Func? = null,
                                  requiresPermission: Func) {
    PermissionRequestType.from(permissions).invoke(
        permissions = permissions,
        activity = requireActivity(),
        onShowRationale = onShowRationale,
        onPermissionDenied = onPermissionDenied,
        onNeverAskAgain = onNeverAskAgain,
        requiresPermission = requiresPermission)
}
