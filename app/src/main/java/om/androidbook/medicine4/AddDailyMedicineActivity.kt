package om.androidbook.medicine4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.EditText

import android.widget.TextView

class AddDailyMedicineActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var countEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_daily_medicine)

        nameEditText = findViewById(R.id.dailyMedicineNameEditText)
        countEditText = findViewById(R.id.dailyMedicineDoseEditText)

        // 등록 버튼 클릭 이벤트 처리
        val registrationButton = findViewById<Button>(R.id.registrationButton)
        registrationButton.setOnClickListener {
            onRegistrationButtonClicked()
        }

        // 홈으로 돌아가는 버튼
        val backHomeButton = findViewById<Button>(R.id.backButton)
        backHomeButton.setOnClickListener {
            val intent = Intent(applicationContext, NaviActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onRegistrationButtonClicked() {
        val useremail = LoginActivity.loggedInUserEmail
        val name: String = nameEditText.text.toString()
        val count: String = countEditText.text.toString()

        if (useremail != null && name.isNotBlank() && count.isNotBlank()) {
            val dbHelper = DBHelper(this, "DRUG_INFO", null, 3)
            val isSuccess = dbHelper.insertDose(useremail, name, count)

            if (isSuccess) {
                // 성공적으로 추가되었을 때의 처리
                Toast.makeText(this, "약 정보가 성공적으로 추가되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                // 추가 실패 시의 처리
                Toast.makeText(this, "약 정보 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // 예외 처리: null 또는 빈 문자열이 발생한 경우
            Toast.makeText(this, "입력 값이 null이거나 빈 문자열입니다.", Toast.LENGTH_SHORT).show()
        }
    }
}