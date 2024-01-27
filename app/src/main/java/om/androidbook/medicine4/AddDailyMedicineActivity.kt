package om.androidbook.medicine4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AddDailyMedicineActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_daily_medicine)

        // 홈으로 돌아가는 버튼
        val backHomeButton = findViewById<Button>(R.id.backButton)
        // 클릭시 홈으로 이동
        backHomeButton.setOnClickListener {
            val intent = Intent(applicationContext, NaviActivity::class.java)
            startActivity(intent)
        }
    }
}