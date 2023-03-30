package edu.uaux.pheart.measure

import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat

class CameraPermissionHelper(
    private val activity: Activity,
    private val onPermissionRequestResult: (granted: Boolean) -> Unit,
) {
    companion object {
        private const val REQUEST_CODE_CAMERA = 420
    }

    /**
     * Whether the app has camera permissions.
     */
    private val hasCameraPermission: Boolean
        get() = ActivityCompat.checkSelfPermission(activity, CAMERA) == PERMISSION_GRANTED

    /**
     * Request camera permissions if they aren't already granted, otherwise run [onPermissionRequestResult] immediately.
     */
    fun requireCameraPermission() {
        if (hasCameraPermission) {
            onPermissionRequestResult(true)
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(CAMERA), REQUEST_CODE_CAMERA)
        }
    }

    /**
     * Callback for [Activity.onRequestPermissionsResult].
     */
    fun handlePermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        require(permissions.size == grantResults.size)
        if (requestCode == REQUEST_CODE_CAMERA) {
            onPermissionRequestResult(hasCameraPermission)
        }
    }
}