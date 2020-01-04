package permissions.dispatcher.ktx

import android.content.Context
import android.content.Intent
import android.net.Uri.fromParts
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.fragment.app.Fragment

/**
 * This fragment holds the single permission request and holds it until the flow is completed
 */
class PermissionsRequestFragment : Fragment() {
    interface PermissionsRequestCallback {
        fun shouldShowRequestPermissionsRationale()
        fun onPermissionsGranted()
        fun onPermissionsPermanentlyDenied()
        fun onPermissionsDenied()
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 199

        @JvmStatic
        fun newInstance() = PermissionsRequestFragment()
    }

    private var callback: PermissionsRequestCallback? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is PermissionsRequestCallback) {
            callback = context
        } else {
            throw IllegalArgumentException()
        }
    }

    fun requestPermissionsFromUser(permissions: Array<String>) {
        requestPermissions(permissions, PERMISSIONS_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun openAppSettings() {
        val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS,
            fromParts("package", activity?.packageName, null))
        startActivityForResult(intent, PERMISSIONS_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}
