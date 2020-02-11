package permissions.dispatcher.ktx

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import permissions.dispatcher.PermissionUtils
import java.lang.ref.WeakReference

typealias Func = () -> Unit
typealias ShowRationaleFunc = (KtxPermissionRequest) -> Unit

fun AppCompatActivity.withPermissionsCheck(vararg permissions: String,
                                           permissionDenied: Func? = null,
                                           showRationale: ShowRationaleFunc? = null,
                                           neverAskAgain: Func? = null,
                                           needsPermission: Func) {
    if (PermissionUtils.hasSelfPermissions(this, *permissions)) {
        needsPermission()
    } else {
        if (PermissionUtils.shouldShowRequestPermissionRationale(this, *permissions)) {
            val funcReference = if (permissionDenied == null) null else WeakReference(permissionDenied)
            val request = KtxPermissionRequest(WeakReference(this), permissions,
                RequestCodeProvider.getAndIncrement(permissions), funcReference)
            showRationale?.invoke(request)
        } else {
            requestPermissions(this, permissions, needsPermission, neverAskAgain, permissionDenied)
        }
    }
}

fun Fragment.withPermissionsCheck(vararg permissions: String,
                                  permissionDenied: Func? = null,
                                  showRationale: ShowRationaleFunc? = null,
                                  neverAskAgain: Func? = null,
                                  needsPermission: Func) {
    if (PermissionUtils.hasSelfPermissions(context, *permissions)) {
        needsPermission()
    } else {
        if (PermissionUtils.shouldShowRequestPermissionRationale(this, *permissions)) {
            val funcReference = if (permissionDenied == null) null else WeakReference(permissionDenied)
            val request = KtxPermissionRequest(WeakReference(this), permissions,
                RequestCodeProvider.getAndIncrement(permissions), funcReference)
            showRationale?.invoke(request)
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
