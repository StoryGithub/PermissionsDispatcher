package permissions.dispatcher.ktx

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import permissions.dispatcher.PermissionUtils

internal fun invoke(permissions: Array<out String>,
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
    if (PermissionUtils.hasSelfPermissions(activity, *permissions)) {
        needsPermission()
    } else {
        if (PermissionUtils.shouldShowRequestPermissionRationale(activity, *permissions)) {
            val request = KtxPermissionRequest.create(permissionDenied) {
                requestPermissions(activity, permissions, needsPermission, neverAskAgain, permissionDenied)
            }
            showRationale?.invoke(request)
        } else {
            requestPermissions(activity, permissions, needsPermission, neverAskAgain, permissionDenied)
        }
    }
}

internal fun requestPermissions(target: Any,
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
        fragment.requestPermissions(permissions, needsPermission, neverAskAgain, onPermissionDenied)
    } else {
        val newFragment = PermissionsRequestFragment.newInstance()
        when (target) {
            is AppCompatActivity ->
                target.supportFragmentManager.beginTransaction()
                    .add(newFragment, PermissionsRequestFragment.tag)
                    .commitNow()
            is Fragment ->
                target.childFragmentManager.beginTransaction()
                    .add(newFragment, PermissionsRequestFragment.tag)
                    .commitNow()
        }
    }
}
