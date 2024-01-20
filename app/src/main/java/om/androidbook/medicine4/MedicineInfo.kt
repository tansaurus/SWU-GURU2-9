package om.androidbook.medicine4

import DatabaseHelper
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast

class MedicineInfoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicine_info)
        val dbHelper = DatabaseHelper(this)

        // 인식된 텍스트 (예시)
        val detectedText = "인식된 제품명"

        val result = dbHelper.searchProduct(detectedText)

        if (result != null) {
            // 화면에 결과 표시
            findViewById<TextView>(R.id.tvProductNameA).text = result
        } else {
            // 일치하는 데이터가 없는 경우
            Toast.makeText(this, "해당하는 제품이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
