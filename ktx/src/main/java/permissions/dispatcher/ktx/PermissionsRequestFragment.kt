package permissions.dispatcher.ktx

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import permissions.dispatcher.PermissionUtils
import permissions.dispatcher.PermissionUtils.verifyPermissions
import java.util.*

internal class PermissionsRequestFragment : Fragment() {
    private val requestCode = Random().nextInt()
    private var requiresPermission: Func? = null
    private var onNeverAskAgain: Func? = null
    private var onPermissionDenied: Func? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        retainInstance = true
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        requiresPermission = null
        onNeverAskAgain = null
        onPermissionDenied = null
    }

    fun requestPermissions(permissions: Array<out String>, requiresPermission: Func,
                           onNeverAskAgain: Func?, onPermissionDenied: Func?) {
        this.requiresPermission = requiresPermission
        this.onNeverAskAgain = onNeverAskAgain
        this.onPermissionDenied = onPermissionDenied
        requestPermissions(permissions, requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == this.requestCode) {
            if (verifyPermissions(*grantResults)) {
                requiresPermission?.invoke()
            } else {
                if (!PermissionUtils.shouldShowRequestPermissionRationale(this, *permissions)) {
                    onNeverAskAgain?.invoke()
                } else {
                    onPermissionDenied?.invoke()
                }
            }
        }
        dismiss()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestOverlayPermission(requiresPermission: Func, onPermissionDenied: Func?) {
        this.requiresPermission = requiresPermission
        this.onPermissionDenied = onPermissionDenied
        requestSpecialPermissions(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestWriteSettingsPermission(requiresPermission: Func, onPermissionDenied: Func?) {
        this.requiresPermission = requiresPermission
        this.onPermissionDenied = onPermissionDenied
        requestSpecialPermissions(Settings.ACTION_MANAGE_WRITE_SETTINGS)
    }

    private fun requestSpecialPermissions(action: String) {
        val uri = Uri.parse("package:${requireContext().packageName}")
        val intent = Intent(action, uri)
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == this.requestCode) {
            if (Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(activity)) {
                requiresPermission?.invoke()
            } else {
                onPermissionDenied?.invoke()
            }
        }
        dismiss()
    }

    private fun dismiss() {
        parentFragmentManager.beginTransaction().remove(this).commitNowAllowingStateLoss()
    }

    companion object {
        val tag = PermissionsRequestFragment::class.java.canonicalName
        fun newInstance() = PermissionsRequestFragment()
    }
}