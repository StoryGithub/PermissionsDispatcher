package permissions.dispatcher.ktx

import androidx.fragment.app.Fragment
import permissions.dispatcher.PermissionUtils

// https://gist.github.com/hotchemi/537d7b2d61b005c1b8e2c57e27dc625f

fun Fragment.withPermissionsCheck(vararg permissions: String,
                                  needsPermission: () -> Unit,
                                  showRationale: (() -> Unit)? = null,
                                  neverAskAgain: (() -> Unit)? = null,
                                  onPermissionDenied: (() -> Unit)? = null) {
    if (PermissionUtils.hasSelfPermissions(this.context, *permissions)) {
        needsPermission.invoke()
    } else {
        if (PermissionUtils.shouldShowRequestPermissionRationale(this, *permissions)) {
            showRationale?.invoke()
        } else {
            requestPermissions(this, permissions, needsPermission, neverAskAgain, onPermissionDenied)
        }
    }
}

private fun requestPermissions(target: Any,
                               permissions: Array<out String>,
                               needsPermission: () -> Unit,
                               neverAskAgain: (() -> Unit)?,
                               onPermissionDenied: (() -> Unit)?) {

    var fragment = when (target) {
        is Fragment -> target.childFragmentManager.findFragmentByTag(
            PermissionsRequestFragment::class.java.canonicalName) as
            PermissionsRequestFragment
        else -> null
    }

    if (fragment == null) {
        fragment = PermissionsRequestFragment.newInstance()
        when (target) {
            is Fragment ->
                target.childFragmentManager.beginTransaction().apply {
                    add(fragment, PermissionsRequestFragment::class.java.canonicalName)
                    commitNow()
                }
        }
    }
    fragment.requestPermissions(permissions, needsPermission, neverAskAgain, onPermissionDenied)
}
