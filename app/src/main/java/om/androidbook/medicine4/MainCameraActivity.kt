package om.androidbook.medicine4

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainCameraActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false
    companion object {
        private const val REQUEST_CODE_CAMERA_PERMISSION = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_camera)

        // 뷰 요소 초기화
        val btnCamera = findViewById<Button>(R.id.btnCamera)
        val btnGallery = findViewById<Button>(R.id.btnGallery)

        // 카메라 버튼 클릭 시 이벤트 처리
        btnCamera.setOnClickListener {
            takePicture()
        }

        // 갤러리 버튼 클릭 시 이벤트 처리
        btnGallery.setOnClickListener {
            openGallery()
        }
        onBackPressedDispatcher.addCallback(this@MainCameraActivity) {
            if (doubleBackToExitPressedOnce) {
                // 앱 종료 로직을 추가할 수 있습니다.
                isEnabled = false // 콜백을 비활성화
                finishAffinity()
            } else {
                // 첫 번째 뒤로가기 버튼 클릭
                Toast.makeText(this@MainCameraActivity, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                doubleBackToExitPressedOnce = true

                // 2초 동안 변수 초기화를 위한 핸들러
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
        }
    }

    private fun takePicture() {
        if (isCameraPermissionGranted()) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureLauncher.launch(takePictureIntent)
        } else {
            requestCameraPermission()
        }
    }
    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CODE_CAMERA_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture()
            } else {
                Toast.makeText(this, "카메라 권한이 주어지지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // 카메라 앱 결과 처리
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            imageBitmap?.let { bitmap ->
                saveImageToGallery(bitmap)
            }
        }
    }

    // 캡처한 이미지 저장
    private fun saveImageToGallery(bitmap: Bitmap) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "captured_image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            // 저장 경로 설정 (Android 10 이상에서는 필수)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let { imageUri ->
            contentResolver.openOutputStream(imageUri)?.use { outputStream ->
                // 이미지를 outputStream을 통해 저장
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            // 미디어 스캐너 실행
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            mediaScanIntent.data = imageUri // 저장한 이미지의 Uri
            sendBroadcast(mediaScanIntent)
        }
    }

    // 갤러리 열기
    private fun openGallery() {
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
    }
}
