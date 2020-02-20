package permissions.dispatcher.ktx

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import permissions.dispatcher.PermissionUtils
import java.lang.ref.WeakReference

internal typealias Func = () -> Unit
typealias ShowRationaleFunc = (KtxPermissionRequest) -> Unit

fun AppCompatActivity.withPermissionsCheck(vararg permissions: String,
                                           showRationale: ShowRationaleFunc? = null,
                                           permissionDenied: Func? = null,
                                           neverAskAgain: Func? = null,
                                           needsPermission: Func) {
    invoke(permissions, this, showRationale, permissionDenied, neverAskAgain, needsPermission)
}

fun Fragment.withPermissionsCheck(vararg permissions: String,
                                  permissionDenied: Func? = null,
                                  showRationale: ShowRationaleFunc? = null,
                                  neverAskAgain: Func? = null,
                                  needsPermission: Func) {
    invoke(permissions, this, showRationale, permissionDenied, neverAskAgain, needsPermission)
}

private fun invoke(permissions: Array<out String>,
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
            val deniedFunc = permissionDenied?.let { WeakReference(it) }
            val requestPermissionsFunc = WeakReference {
                requestPermissions(activity, permissions, needsPermission, neverAskAgain,
                    permissionDenied)
            }
            showRationale?.invoke(KtxPermissionRequest(requestPermissionsFunc, deniedFunc))
        } else {
            requestPermissions(activity, permissions, needsPermission, neverAskAgain, permissionDenied)
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
            PermissionsRequestFragment::class.java.canonicalName) as? PermissionsRequestFragment
        is Fragment -> target.childFragmentManager.findFragmentByTag(
            PermissionsRequestFragment::class.java.canonicalName) as? PermissionsRequestFragment
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
