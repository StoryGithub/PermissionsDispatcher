package permissions.dispatcher.ktx

import android.Manifest
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import permissions.dispatcher.PermissionUtils.hasSelfPermissions
import permissions.dispatcher.PermissionUtils.shouldShowRequestPermissionRationale

internal sealed class PermissionRequestType {
    object SystemAlertWindow : PermissionRequestType() {
        override fun checkSelfPermission(context: Context, permissions: Array<out String>): Boolean =
            Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(context)

        @RequiresApi(Build.VERSION_CODES.M)
        override fun requestDelegate(fragment: PermissionsRequestFragment,
                                     permissions: Array<out String>,
                                     needsPermission: Func,
                                     neverAskAgain: Func?,
                                     onPermissionDenied: Func?) =
            fragment.requestOverlayPermission(permissions, needsPermission, neverAskAgain, onPermissionDenied)
    }

    object WriteSettings : PermissionRequestType() {
        override fun checkSelfPermission(context: Context, permissions: Array<out String>): Boolean =
            Build.VERSION.SDK_INT < 23 || Settings.System.canWrite(context)

        @RequiresApi(Build.VERSION_CODES.M)
        override fun requestDelegate(fragment: PermissionsRequestFragment,
                                     permissions: Array<out String>,
                                     needsPermission: Func,
                                     neverAskAgain: Func?,
                                     onPermissionDenied: Func?) =
            fragment.requestWriteSettingsPermission(permissions, needsPermission, neverAskAgain, onPermissionDenied)
    }

    object Others : PermissionRequestType() {
        override fun checkSelfPermission(context: Context, permissions: Array<out String>): Boolean =
            hasSelfPermissions(context, *permissions)

        override fun requestDelegate(fragment: PermissionsRequestFragment,
                                     permissions: Array<out String>,
                                     needsPermission: Func,
                                     neverAskAgain: Func?,
                                     onPermissionDenied: Func?) =
            fragment.requestPermissions(permissions, needsPermission, neverAskAgain, onPermissionDenied)
    }

    abstract fun checkSelfPermission(context: Context, permissions: Array<out String>): Boolean

    abstract fun requestDelegate(fragment: PermissionsRequestFragment,
                                 permissions: Array<out String>,
                                 needsPermission: Func,
                                 neverAskAgain: Func?,
                                 onPermissionDenied: Func?)

    private fun requestPermissions(permissions: Array<out String>,
                                   target: Any,
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
            requestDelegate(fragment, permissions, needsPermission, neverAskAgain, onPermissionDenied)
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

    fun invoke(permissions: Array<out String>,
               target: Any,
               showRationale: ShowRationaleFunc?,
               permissionDenied: Func?,
               neverAskAgain: Func?,
               needsPermission: Func) {
        val activity = when (target) {
            is AppCompatActivity -> target
            is Fragment -> target.activity
            else -> null
        } ?: return
        if (checkSelfPermission(activity, permissions)) {
            needsPermission()
        } else {
            if (shouldShowRequestPermissionRationale(activity, *permissions)) {
                showRationale?.invoke(KtxPermissionRequest.create(permissionDenied) {
                    requestPermissions(permissions, activity, needsPermission, neverAskAgain, permissionDenied)
                })
            } else {
                requestPermissions(permissions, activity, needsPermission, neverAskAgain, permissionDenied)
            }
        }
    }

    companion object {
        fun create(permissions: Array<out String>): PermissionRequestType {
            return if (permissions.size == 1) {
                when (permissions.first()) {
                    Manifest.permission.SYSTEM_ALERT_WINDOW -> SystemAlertWindow
                    Manifest.permission.WRITE_SETTINGS -> WriteSettings
                    else -> Others
                }
            } else {
                Others
            }
        }
    }
}
