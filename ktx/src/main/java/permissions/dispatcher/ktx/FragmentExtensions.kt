package permissions.dispatcher.ktx

import android.Manifest
import androidx.fragment.app.Fragment

fun Fragment.withPermissionsCheck(
    vararg permissions: String,
    onShowRationale: ShowRationaleFunc? = null,
    onPermissionDenied: Func? = null,
    onNeverAskAgain: Func? = null,
    requiresPermission: Func) {
    PermissionRequestType.Others.invoke(
        permissions = permissions,
        activity = requireActivity(),
        onShowRationale = onShowRationale,
        onPermissionDenied = onPermissionDenied,
        onNeverAskAgain = onNeverAskAgain,
        requiresPermission = requiresPermission)
}

fun Fragment.withWriteSettingsPermissionCheck(
    onShowRationale: ShowRationaleFunc? = null,
    onPermissionDenied: Func? = null,
    requiresPermission: Func) {
    PermissionRequestType.WriteSettings.invoke(
        permissions = arrayOf(Manifest.permission.WRITE_SETTINGS),
        activity = requireActivity(),
        onShowRationale = onShowRationale,
        onPermissionDenied = onPermissionDenied,
        onNeverAskAgain = null,
        requiresPermission = requiresPermission)
}

fun Fragment.withSystemAlertWindowPermissionCheck(
    onShowRationale: ShowRationaleFunc? = null,
    onPermissionDenied: Func? = null,
    requiresPermission: Func) {
    PermissionRequestType.SystemAlertWindow.invoke(
        permissions = arrayOf(Manifest.permission.SYSTEM_ALERT_WINDOW),
        activity = requireActivity(),
        onShowRationale = onShowRationale,
        onPermissionDenied = onPermissionDenied,
        onNeverAskAgain = null,
        requiresPermission = requiresPermission)
}
