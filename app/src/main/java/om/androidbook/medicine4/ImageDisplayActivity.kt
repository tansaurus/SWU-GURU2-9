package om.androidbook.medicine4

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageDisplayActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)

        dbHelper = DBHelper(this, "drug_info", null, 1)
        val recognizedText = intent.getStringExtra("DetectedText") ?: "기본 텍스트" // 여기에 OCR로 인식된 텍스트를 넣습니다.
        textView = findViewById(R.id.textView)
        searchDrugInfo(recognizedText)
        val imageView = findViewById<ImageView>(R.id.imageview)
        // 인텐트에서 이미지 URI 및 텍스트 데이터를 가져옵니다.
        val imageUri = intent.getStringExtra("imageUri")

        // 이미지 URI가 유효한 경우, 이미지 표시
        if (!imageUri.isNullOrEmpty()) {
            imageView.setImageURI(Uri.parse(imageUri))
        } else {
            // 이미지 URI가 없는 경우, 기본 이미지 표시
            imageView.setImageResource(R.drawable.ic_launcher_foreground) // 예시로 default_image라는 리소스 이름을 사용했습니다.
        }

    }
    @SuppressLint("Range")
    private fun searchDrugInfo(drugName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val cursor = dbHelper.getDrugInfo(this@ImageDisplayActivity, drugName)
            val result = if (cursor != null && cursor.moveToFirst()) {
                // 데이터베이스에서 정보를 읽어와서 문자열로 변환
                val info = "약품명: ${cursor.getString(cursor.getColumnIndex("약품명"))}\n\n" +
                        "효능군: ${cursor.getString(cursor.getColumnIndex("효능군"))}\n\n" +
                        "1일 최대투여량: ${cursor.getString(cursor.getColumnIndex("1일 최대투여량"))}\n\n" +
                        "같이 복용하면 안되는 성분명: ${cursor.getString(cursor.getColumnIndex("성분명B"))}\n\n" +
                        "금기사유: ${cursor.getString(cursor.getColumnIndex("금기사유"))}"
                cursor.close()
                info
            } else {
                "약품 정보를 찾을 수 없습니다."
            }

            // 메인 스레드에서 UI 업데이트
            withContext(Dispatchers.Main) {
                textView.text = "인식된 텍스트: $drugName\n\n$result"
            }
        }
    }
}
