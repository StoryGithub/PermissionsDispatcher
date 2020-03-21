package permissions.dispatcher.ktx

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import permissions.dispatcher.PermissionUtils
import permissions.dispatcher.PermissionUtils.verifyPermissions

internal class PermissionsRequestFragment : Fragment() {
    private var permissions: Array<out String>? = null
    private var requiresPermission: Func? = null
    private var onNeverAskAgain: Func? = null
    private var onPermissionDenied: Func? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        retainInstance = true
    }

    fun requestPermissions(permissions: Array<out String>,
                           requiresPermission: Func,
                           onNeverAskAgain: Func?,
                           onPermissionDenied: Func?) {
        this.requiresPermission = requiresPermission
        this.onNeverAskAgain = onNeverAskAgain
        this.onPermissionDenied = onPermissionDenied
        requestPermissions(permissions, RequestCodeProvider.getAndIncrement(permissions))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (verifyPermissions(*grantResults)) {
            requiresPermission?.invoke()
        } else {
            if (PermissionUtils.shouldShowRequestPermissionRationale(this, *permissions).not()) {
                onNeverAskAgain?.invoke()
            } else {
                onPermissionDenied?.invoke()
            }
        }
        activity?.supportFragmentManager?.popBackStack()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestOverlayPermission(permissions: Array<out String>,
                                 requiresPermission: Func,
                                 onNeverAskAgain: Func?,
                                 onPermissionDenied: Func?) {
        this.permissions = permissions
        this.requiresPermission = requiresPermission
        this.onNeverAskAgain = onNeverAskAgain
        this.onPermissionDenied = onPermissionDenied
        requestSpecialPermissions(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, permissions)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestWriteSettingsPermission(permissions: Array<out String>,
                                       requiresPermission: Func,
                                       onNeverAskAgain: Func?,
                                       onPermissionDenied: Func?) {
        this.permissions = permissions
        this.requiresPermission = requiresPermission
        this.onNeverAskAgain = onNeverAskAgain
        this.onPermissionDenied = onPermissionDenied
        requestSpecialPermissions(Settings.ACTION_MANAGE_WRITE_SETTINGS, permissions)
    }

    private fun requestSpecialPermissions(action: String, permissions: Array<out String>) {
        val uri = Uri.parse("package:${requireContext().packageName}")
        val intent = Intent(action, uri)
        startActivityForResult(intent, RequestCodeProvider.getAndIncrement(permissions))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val permissions = permissions ?: return
        when (requestCode) {
            RequestCodeProvider.get(permissions) ->
                if (Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(activity)) {
                    requiresPermission?.invoke()
                } else {
                    onPermissionDenied?.invoke()
                }
        }
        activity?.supportFragmentManager?.popBackStack()
    }

    companion object {
        val tag = PermissionsRequestFragment::class.java.canonicalName
        fun newInstance() = PermissionsRequestFragment()
    }
}