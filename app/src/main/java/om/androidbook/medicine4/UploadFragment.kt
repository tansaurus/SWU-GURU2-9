package om.androidbook.medicine4

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import java.io.IOException

@Suppress("DEPRECATION")
class UploadFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE_CAMERA_PERMISSION = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 프래그먼트의 레이아웃을 inflate하여 반환
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 카메라 버튼
        val btnCamera = view.findViewById<Button>(R.id.btnCamera)
        // 갤러리 버튼
        val btnGallery = view.findViewById<Button>(R.id.btnGallery)

        // 카메라 버튼 클릭 시 이벤트
        btnCamera.setOnClickListener {
            takePicture()
        }

        // 갤러리 버튼 클릭 시 이벤트
        btnGallery.setOnClickListener {
            openGallery()
        }
    }

    // 카메라 사용 메소드
    private fun takePicture() {
        // 카메라 권한이 허용
        if (isCameraPermissionGranted()) {
            // 카메라 앱 호출
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // 이미지를 촬영하고 결과를 처리
            takePictureLauncher.launch(takePictureIntent)
        }

        // 카메라 권한 비허용
        else {
            // 권한 요청
            requestCameraPermission()
        }
    }

    // 카메라 권한 허용 확인 메서드
    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 카메라 권한 요청 메서드
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CODE_CAMERA_PERMISSION
        )
    }

    // 권한 요청 결과 처리 메서드
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION && grantResults.isNotEmpty()) {
            // 권한 허용시 사진 촬영
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture()
            }

            // 권한 거부시 메시지 출력
            else {
                Toast.makeText(requireContext(), "카메라 권한이 주어지지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 카메라 앱 결과 처리
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            imageBitmap?.let { bitmap ->
                // 촬영한 이미지의 URI를 얻어오기
                val imageUri = saveImageAndGetUri(bitmap)
                // 이미지를 텍스트로 인식
                recognizeTextFromImage(imageUri)
            }
        }
    }

    // 촬영한 이미지를 갤러리에 저장하고 이미지의 URI를 반환하는 메소드
    private fun saveImageAndGetUri(bitmap: Bitmap): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "captured_image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        // 이미지를 갤러리에 저장하고 저장된 이미지의 URI를 반환
        val uri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let { imageUri ->
            // 이미지를 저장한 후, 미디어 스캐너를 통해 갤러리에 반영되도록 함
            requireContext().contentResolver.openOutputStream(imageUri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            // 미디어 스캐너 실행
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            mediaScanIntent.data = imageUri
            requireContext().sendBroadcast(mediaScanIntent)
            return imageUri
        }
        return null
    }

    // 텍스트 분석 메소드
    private fun recognizeTextFromImage(imageUri: Uri?) {
        imageUri?.let { uri ->
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                val image = InputImage.fromBitmap(bitmap, 0)
                val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        // 인식된 텍스트와 이미지 URI를 ImageDisplayActivity로 전달
                        val detectedText = visionText.text
                        val intent = Intent(requireContext(), ImageDisplayActivity::class.java).apply {
                            putExtra("DetectedText", detectedText)
                            putExtra("imageUri", uri.toString()) // 이미지 URI 추가
                        }
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        // 인식에 실패한 경우의 처리
                        Toast.makeText(requireContext(), "실패했습니다!", Toast.LENGTH_SHORT).show()
                        e.printStackTrace() // 오류 로깅
                    }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // 갤러리 열기 메서드
    private fun openGallery() {
        val intent = Intent(requireContext(), GalleryActivity::class.java)
        startActivity(intent)
    }
}