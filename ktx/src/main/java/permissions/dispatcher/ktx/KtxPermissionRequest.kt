package permissions.dispatcher.ktx

import permissions.dispatcher.PermissionRequest
import java.lang.ref.WeakReference

class KtxPermissionRequest(
    private val requestPermission: WeakReference<Func>,
    private val permissionDenied: WeakReference<Func>?
) : PermissionRequest {
    override fun proceed() {
        requestPermission.get()?.invoke()
    }

    override fun cancel() {
        permissionDenied?.get()?.invoke()
    }
}
