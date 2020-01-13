package permissions.dispatcher.ktx

import androidx.fragment.app.Fragment
import permissions.dispatcher.PermissionUtils

class PermissionsRequestFragment : Fragment() {
    private var needsPermission: (() -> Unit)? = null
    private var neverAskAgain: (() -> Unit)? = null
    private var onPermissionDenied: (() -> Unit)? = null

    fun requestPermissions(permissions: Array<out String>,
                                   needsPermission: () -> Unit,
                                   neverAskAgain: (() -> Unit)?,
                                   onPermissionDenied: (() -> Unit)?) {
        this.needsPermission = needsPermission
        this.neverAskAgain = neverAskAgain
        this.onPermissionDenied = onPermissionDenied
        requestPermissions(permissions, RequestCodeProvider.nextRequestCode(permissions))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (RequestCodeProvider.nextRequestCode(permissions) > 0) {
            if (PermissionUtils.verifyPermissions(*grantResults)) {
                needsPermission?.invoke()
            } else {
                if (!PermissionUtils.shouldShowRequestPermissionRationale(this, *permissions)) {
                    neverAskAgain?.invoke()
                } else {
                    onPermissionDenied?.invoke()
                }
            }
        }
    }

    companion object {
        fun newInstance() = PermissionsRequestFragment()
    }
}
