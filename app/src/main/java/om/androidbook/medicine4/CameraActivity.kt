package om.androidbook.medicine4

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.cloud.vision.v1.ImageAnnotatorClient
import om.androidbook.medicine4.GalleryActivity
import om.androidbook.medicine4.databinding.ActivityMainCameraBinding
import java.io.IOException
import java.io.InputStream

class CameraActivity : AppCompatActivity() {
    companion object {
        const val CAMERA_REQUEST_CODE = 123 // 원하는 숫자로 설정
    }

    private var imageUri: Uri? = null
    private val binding by lazy { ActivityMainCameraBinding.inflate(layoutInflater) }
    private lateinit var visionClient: ImageAnnotatorClient

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                // 권한이 거부되었을 때 처리
                // 예: Toast 메시지 표시, 기능 제한, 앱 종료 등
                Toast.makeText(this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Google Cloud Vision API 클라이언트 초기화
        initVisionClient()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없다면 사용자에게 요청
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            // 권한이 있다면 카메라 열기
            openCamera()
        }
    }

    private fun initVisionClient() {
        try {
            // TODO: JSON 파일 경로를 수정하세요.
            val credentialsStream: InputStream = assets.open("D:\\AndroidStudio\\Medicine4\\medicine-410902-a43bb33902b2.json")
            visionClient = ImageAnnotatorClient.create()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun openCamera() {
        // 카메라 앱을 호출하여 사진을 촬영합니다.
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "photo.jpg")
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        val imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        imageUri = contentResolver.insert(imageCollection, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    private fun displayText(text: String) {
        // 텍스트를 화면에 표시하거나 필요한 처리를 수행하세요.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // 이미지를 촬영하고 저장한 후 호출됩니다.
            if (imageUri != null) {
                // 저장된 이미지를 갤러리에 추가
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = imageUri
                sendBroadcast(mediaScanIntent)

                // GalleryActivity로 이동
                val galleryIntent = Intent(this, GalleryActivity::class.java)
                galleryIntent.putExtra("imageUri", imageUri.toString())
                startActivity(galleryIntent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        visionClient.close()
    }
}
