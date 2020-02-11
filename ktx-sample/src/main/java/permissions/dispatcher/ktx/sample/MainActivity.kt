package permissions.dispatcher.ktx.sample

import android.Manifest
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import permissions.dispatcher.ktx.KtxPermissionRequest
import permissions.dispatcher.ktx.withPermissionsCheck
import permissions.dispatcher.ktx.sample.camera.CameraPreviewFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val buttonCamera: Button = findViewById(R.id.button_camera)
        buttonCamera.setOnClickListener {
            showCamera()
        }
    }

    private fun showCamera() = withPermissionsCheck(Manifest.permission.CAMERA,
        showRationale = ::onCameraShowRationale,
        permissionDenied = ::onCameraDenied,
        neverAskAgain = ::onCameraNeverAskAgain) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.sample_content_fragment, CameraPreviewFragment.newInstance())
            .addToBackStack("camera")
            .commitAllowingStateLoss()
    }

    private fun onCameraDenied() {
        // NOTE: Deal with a denied permission, e.g. by showing specific UI
        // or disabling certain functionality
        Toast.makeText(this, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show()
    }

    private fun onCameraShowRationale(request: KtxPermissionRequest) {
        request.proceed()
        Toast.makeText(this, "onCameraShowRationale", Toast.LENGTH_SHORT).show()
    }

    private fun onCameraNeverAskAgain() {
        Toast.makeText(this, "onCameraNeverAskAgain", Toast.LENGTH_SHORT).show()
    }
}
