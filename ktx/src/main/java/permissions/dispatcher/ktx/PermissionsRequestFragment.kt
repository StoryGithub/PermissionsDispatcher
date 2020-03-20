package permissions.dispatcher.ktx

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import permissions.dispatcher.PermissionUtils

internal class PermissionsRequestFragment : Fragment() {
    private var permissions: Array<out String>? = null
    private var needsPermission: Func? = null
    private var neverAskAgain: Func? = null
    private var onPermissionDenied: Func? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        retainInstance = true
    }

    fun requestPermissions(permissions: Array<out String>,
                           needsPermission: Func,
                           neverAskAgain: Func?,
                           onPermissionDenied: Func?) {
        this.needsPermission = needsPermission
        this.neverAskAgain = neverAskAgain
        this.onPermissionDenied = onPermissionDenied
        requestPermissions(permissions, RequestCodeProvider.getAndIncrement(permissions))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtils.verifyPermissions(*grantResults)) {
            needsPermission?.invoke()
        } else {
            if (PermissionUtils.shouldShowRequestPermissionRationale(this, *permissions).not()) {
                neverAskAgain?.invoke()
            } else {
                onPermissionDenied?.invoke()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestOverlayPermission(permissions: Array<out String>,
                                 needsPermission: Func,
                                 neverAskAgain: Func?,
                                 onPermissionDenied: Func?) {
        this.permissions = permissions
        this.needsPermission = needsPermission
        this.neverAskAgain = neverAskAgain
        this.onPermissionDenied = onPermissionDenied
        val uri = Uri.parse("package:${requireContext().packageName}")
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
        startActivityForResult(intent, RequestCodeProvider.getAndIncrement(permissions))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val permissions = permissions ?: return
        when (requestCode) {
            RequestCodeProvider.get(permissions) ->
                if (Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(activity)) {
                    needsPermission?.invoke()
                } else {
                    onPermissionDenied?.invoke()
                }
        }
    }

    internal companion object {
        val tag = PermissionsRequestFragment::class.java.canonicalName
        fun newInstance() = PermissionsRequestFragment()
    }
}