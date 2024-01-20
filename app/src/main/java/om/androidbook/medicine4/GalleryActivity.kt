package om.androidbook.medicine4

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException
import java.util.Arrays

class GalleryActivity : AppCompatActivity() {

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                imageUri?.let { uri ->
                    recognizeTextFromImage(uri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        // 갤러리 열기
        openGallery()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    private fun recognizeTextFromImage(imageUri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())


            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // 인식된 텍스트와 이미지 URI를 ImageDisplayActivity로 전달합니다.
                    val detectedText = visionText.text
                    val intent = Intent(this, ImageDisplayActivity::class.java).apply {
                        putExtra("DetectedText", detectedText)
                        putExtra("imageUri", imageUri.toString()) // 이미지 URI 추가
                    }
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    // 인식에 실패한 경우의 처리
                    Toast.makeText(this, "실패했습니다!", Toast.LENGTH_SHORT).show()
                    e.printStackTrace() // 오류 로깅
                }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
