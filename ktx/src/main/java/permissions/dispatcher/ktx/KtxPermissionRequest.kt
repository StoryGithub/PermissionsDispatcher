package permissions.dispatcher.ktx

import permissions.dispatcher.PermissionRequest
import java.lang.ref.WeakReference

internal class KtxPermissionRequest(
    private val requestPermission: WeakReference<Func>,
    private val permissionDenied: WeakReference<Func>?
) : PermissionRequest {
    override fun proceed() {
        requestPermission.get()?.invoke()
    }

    override fun cancel() {
        permissionDenied?.get()?.invoke()
    }

    internal companion object {
        fun create(permissionDenied: Func?, requestPermission: Func): PermissionRequest =
            KtxPermissionRequest(
                requestPermission = WeakReference(requestPermission),
                permissionDenied = permissionDenied?.let { WeakReference(it) }
            )
    }
}
