package permissions.dispatcher.ktx

import android.Manifest
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.withPermissionsCheck(
    vararg permissions: String,
    onShowRationale: ShowRationaleFunc? = null,
    onPermissionDenied: Func? = null,
    onNeverAskAgain: Func? = null,
    requiresPermission: Func) {
    PermissionRequestType.Others.invoke(
        permissions = permissions,
        activity = this,
        onShowRationale = onShowRationale,
        onPermissionDenied = onPermissionDenied,
        onNeverAskAgain = onNeverAskAgain,
        requiresPermission = requiresPermission)
}

fun FragmentActivity.withWriteSettingsPermissionCheck(
    onShowRationale: ShowRationaleFunc? = null,
    onPermissionDenied: Func? = null,
    requiresPermission: Func) {
    PermissionRequestType.WriteSettings.invoke(
        permissions = arrayOf(Manifest.permission.WRITE_SETTINGS),
        activity = this,
        onShowRationale = onShowRationale,
        onPermissionDenied = onPermissionDenied,
        onNeverAskAgain = null,
        requiresPermission = requiresPermission)
}

fun FragmentActivity.withSystemAlertWindowPermissionCheck(
    onShowRationale: ShowRationaleFunc? = null,
    onPermissionDenied: Func? = null,
    requiresPermission: Func) {
    PermissionRequestType.SystemAlertWindow.invoke(
        permissions = arrayOf(Manifest.permission.SYSTEM_ALERT_WINDOW),
        activity = this,
        onShowRationale = onShowRationale,
        onPermissionDenied = onPermissionDenied,
        onNeverAskAgain = null,
        requiresPermission = requiresPermission)
}
