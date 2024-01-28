package om.androidbook.medicine4

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)

        dbHelper = DBHelper(this, "DRUG_INFO", null, 2)
        textView = findViewById(R.id.drugNameTextView)
        val recognizedText = intent.getStringExtra("DetectedText")
        if (recognizedText.isNullOrEmpty()) {
            textView.text = "인식된 텍스트가 없습니다."
        }
        else{
            searchDrugInfo(recognizedText)
        }

        if (!recognizedText.isNullOrEmpty()) {
            searchDrugInfo(recognizedText)
        } else {
            textView.text = "인식된 텍스트가 없습니다."
        }


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

        val goodHealthButton = findViewById<Button>(R.id.goodHealthButton)
        val dangerButton = findViewById<Button>(R.id.dangerButton)


        val therapeuticGroupTextView = findViewById<TextView>(R.id.therapeuticGroupTextView)

        val maxDailyDosageTextView = findViewById<TextView>(R.id.maxDailyDosageTextView)
        val ingredientNameTextView = findViewById<TextView>(R.id.ingredientNameTextView)
        val contraindicationsTextView = findViewById<TextView>(R.id.contraindicationsTextView)
        val bluegoodHealthButton = findViewById<Button>(R.id.selectgoodButton)
        val bluedangerButton = findViewById<Button>(R.id.selectdangerButton)


        goodHealthButton.setOnClickListener {

            therapeuticGroupTextView.visibility = View.VISIBLE
            maxDailyDosageTextView.visibility = View.VISIBLE
            dangerButton.visibility = View.VISIBLE
            bluegoodHealthButton.visibility = View.VISIBLE

            ingredientNameTextView.visibility = View.GONE
            contraindicationsTextView.visibility = View.GONE

            bluedangerButton.visibility = View.GONE
            goodHealthButton.visibility = View.GONE

        }

        // 복용금기 보이기 버튼 클릭 시
        dangerButton.setOnClickListener {
            contraindicationsTextView.visibility = View.VISIBLE
            ingredientNameTextView.visibility = View.VISIBLE
            bluedangerButton.visibility = View.VISIBLE
            goodHealthButton.visibility = View.VISIBLE

            maxDailyDosageTextView.visibility = View.GONE
            therapeuticGroupTextView.visibility = View.GONE

            dangerButton.visibility = View.GONE
            bluegoodHealthButton.visibility = View.GONE
        }

        // 홈으로 돌아가는 버튼
        val backHomeButton = findViewById<Button>(R.id.backHomeButton)
        // 클릭시 홈으로 이동
        backHomeButton.setOnClickListener {
            val intent = Intent(applicationContext, NaviActivity::class.java)
            startActivity(intent)
        }
    }
    @SuppressLint("Range")
    private fun searchDrugInfo(drugName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val cursor = dbHelper.getDrugInfo(this@ImageDisplayActivity, drugName)
            val result = if (cursor != null && cursor.moveToFirst()) {

                val drugName = cursor.getString(cursor.getColumnIndex("DRUG_NAME"))
                val therapeuticGroup = cursor.getString(cursor.getColumnIndex("THERAPEUTIC_GROUP"))
                val maxDailyDosage = cursor.getString(cursor.getColumnIndex("MAX_DAILY_DOSAGE"))
                val ingredientName = cursor.getString(cursor.getColumnIndex("INGREDIENT_NAME"))
                val contraindications = cursor.getString(cursor.getColumnIndex("CONTRAINDICATIONS"))
                // 데이터베이스에서 정보를 읽어와서 문자열로 변환
//                val info = "약품명: ${cursor.getString(cursor.getColumnIndex("DRUG_NAME"))}\n\n" +
//                        "효능군: ${cursor.getString(cursor.getColumnIndex("THERAPEUTIC_GROUP"))}\n\n" +
//                        "1일 최대투여량: ${cursor.getString(cursor.getColumnIndex("MAX_DAILY_DOSAGE"))}\n\n" +
//                        "같이 복용하면 안되는 성분명: ${cursor.getString(cursor.getColumnIndex("INGREDIENT_NAME"))}\n\n" +
//                        "금기사유: ${cursor.getString(cursor.getColumnIndex("CONTRAINDICATIONS"))}"
                cursor.close()
                val drugNameTextView = findViewById<TextView>(R.id.drugNameTextView)
                drugNameTextView.text = "$drugName" // 약품명

                val therapeuticGroupTextView =
                    findViewById<TextView>(R.id.therapeuticGroupTextView)
                therapeuticGroupTextView.text = "이런 약이에요 - "

                val maxDailyDosageTextView = findViewById<TextView>(R.id.maxDailyDosageTextView)
                maxDailyDosageTextView.text = "효능군: $therapeuticGroup \n\n1일 최대투여량: $maxDailyDosage"   // 효능군, 1일 최대투여량

                val ingredientNameTextView = findViewById<TextView>(R.id.ingredientNameTextView)
                ingredientNameTextView.text = "같이 복용하면 안되는 성분이 있어요 - "

                val contraindicationsTextView =
                    findViewById<TextView>(R.id.contraindicationsTextView)
                contraindicationsTextView.text = "병용금기 성분명: $ingredientName \n\n금기사유: $contraindications"  //병용금기 성분명, 금기사유


            } else {
                Log.d("ImageDisplayActivity", "약품 정보를 찾을 수 없음: $drugName")
                val drugNameTextView = findViewById<TextView>(R.id.drugNameTextView)
                drugNameTextView.text = "약품 정보를 찾을 수 없습니다"
            }


        }
    }



}