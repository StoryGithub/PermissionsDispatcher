package permissions.dispatcher.ktx

import android.app.Activity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import permissions.dispatcher.PermissionRequest
import java.lang.ref.WeakReference

class KtxPermissionRequest(
    private val target: WeakReference<Any>,
    private val permissions: Array<out String>,
    private val requestCode: Int,
    private val permissionDenied: WeakReference<Func>? = null
) : PermissionRequest {
    override fun proceed() {
        target.get()?.also {
            when (it) {
                is Activity -> ActivityCompat.requestPermissions(it, permissions, requestCode)
                is Fragment -> it.requestPermissions(permissions, requestCode)
            }
        }
    }

    override fun cancel() {
        permissionDenied?.get()?.invoke()
    }
}
