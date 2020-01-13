package permissions.dispatcher.ktx

import androidx.fragment.app.Fragment
import permissions.dispatcher.PermissionUtils

internal class PermissionsRequestFragment : Fragment() {
    private var needsPermission: Func? = null
    private var neverAskAgain: Func? = null
    private var onPermissionDenied: Func? = null

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
        if (RequestCodeProvider.containsKey(permissions)) {
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
    }

    companion object {
        fun newInstance() = PermissionsRequestFragment()
    }
}
