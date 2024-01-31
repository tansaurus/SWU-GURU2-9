package om.androidbook.medicine4

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity

class AddDailyMedicineActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var countEditText: EditText
    private var doubleBackToExitPressedOnce = false

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
        onBackPressedDispatcher.addCallback(this@AddDailyMedicineActivity) {
            if (doubleBackToExitPressedOnce) {
                // 앱 종료 로직을 추가할 수 있습니다.
                isEnabled = false // 콜백을 비활성화
                finishAffinity()
            } else {
                // 첫 번째 뒤로가기 버튼 클릭
                Toast.makeText(this@AddDailyMedicineActivity, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                doubleBackToExitPressedOnce = true

                // 2초 동안 변수 초기화를 위한 핸들러
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
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
                // 홈으로 이동
                val intent = Intent(applicationContext, NaviActivity::class.java)
                startActivity(intent)
            } else {
                // 추가 실패 시의 처리
                Toast.makeText(this, "약 정보 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // 예외 처리: null 또는 빈 문자열이 발생한 경우
            Toast.makeText(this, "약 정보를 모두 입력해 주세요.", Toast.LENGTH_SHORT).show()
        }
    }
}