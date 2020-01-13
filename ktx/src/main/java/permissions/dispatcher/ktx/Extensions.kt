package permissions.dispatcher.ktx

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import permissions.dispatcher.PermissionUtils

typealias Func = () -> Unit

fun AppCompatActivity.withPermissionsCheck(vararg permissions: String,
                                           permissionDenied: Func? = null,
                                           showRationale: Func? = null,
                                           neverAskAgain: Func? = null,
                                           needsPermission: Func) {
    if (PermissionUtils.hasSelfPermissions(this, *permissions)) {
        needsPermission.invoke()
    } else {
        if (PermissionUtils.shouldShowRequestPermissionRationale(this, *permissions)) {
            showRationale?.invoke()
        } else {
            requestPermissions(this, permissions, needsPermission, neverAskAgain, permissionDenied)
        }
    }
}

fun Fragment.withPermissionsCheck(vararg permissions: String,
                                  permissionDenied: Func? = null,
                                  showRationale: Func? = null,
                                  neverAskAgain: Func? = null,
                                  needsPermission: Func) {
    if (PermissionUtils.hasSelfPermissions(this.context, *permissions)) {
        needsPermission.invoke()
    } else {
        if (PermissionUtils.shouldShowRequestPermissionRationale(this, *permissions)) {
            showRationale?.invoke()
        } else {
            requestPermissions(this, permissions, needsPermission, neverAskAgain, permissionDenied)
        }
    }
}

private fun requestPermissions(target: Any,
                               permissions: Array<out String>,
                               needsPermission: Func,
                               neverAskAgain: Func?,
                               onPermissionDenied: Func?) {
    var fragment = when (target) {
        is AppCompatActivity -> target.supportFragmentManager.findFragmentByTag(
            PermissionsRequestFragment::class.java.canonicalName) as PermissionsRequestFragment
        is Fragment -> target.childFragmentManager.findFragmentByTag(
            PermissionsRequestFragment::class.java.canonicalName) as PermissionsRequestFragment
        else -> null
    }

    if (fragment == null) {
        fragment = PermissionsRequestFragment.newInstance()
        when (target) {
            is AppCompatActivity ->
                target.supportFragmentManager.beginTransaction().apply {
                    add(fragment, PermissionsRequestFragment::class.java.canonicalName)
                    commitNow()
                }
            is Fragment ->
                target.childFragmentManager.beginTransaction().apply {
                    add(fragment, PermissionsRequestFragment::class.java.canonicalName)
                    commitNow()
                }
        }
    }
    fragment.requestPermissions(permissions, needsPermission, neverAskAgain, onPermissionDenied)
}
