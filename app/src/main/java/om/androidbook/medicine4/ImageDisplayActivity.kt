package om.androidbook.medicine4

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ImageDisplayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)

        val imageView = findViewById<ImageView>(R.id.imageview)
        val textView = findViewById<TextView>(R.id.textView)

        // 인텐트에서 이미지 URI 및 텍스트 데이터를 가져옵니다.
        val imageUri = intent.getStringExtra("imageUri")
        val detectedText = intent.getStringExtra("DetectedText") ?: "기본 텍스트"
        textView.text = detectedText

// 텍스트 중 제품명A 확인
        if (detectedText.contains("제품명A")) { // '제품명A'를 실제 찾고자 하는 제품명으로 변경해야 합니다.
            val intent = Intent(this, MedicineInfoActivity::class.java)
            intent.putExtra("productNameA", "제품명A") // 실제 찾은 제품명A를 넘겨줍니다.
            startActivity(intent)
        } else {
            Toast.makeText(this, "해당하는 제품이 없습니다.", Toast.LENGTH_SHORT).show()
        }

        // 로그 출력
        Log.d("ImageDisplayActivity", "imageUri: $imageUri")
        Log.d("ImageDisplayActivity", "detectedText: $detectedText")

        // 이미지 URI가 유효한 경우, 이미지 표시
        if (!imageUri.isNullOrEmpty()) {
            imageView.setImageURI(Uri.parse(imageUri))
        } else {
            // 이미지 URI가 없는 경우, 기본 이미지 표시
            imageView.setImageResource(R.drawable.ic_launcher_foreground) // 예시로 default_image라는 리소스 이름을 사용했습니다.
        }

        // 텍스트 표시
        textView.text = detectedText
    }
}
