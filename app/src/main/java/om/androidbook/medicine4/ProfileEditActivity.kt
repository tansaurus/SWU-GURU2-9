package om.androidbook.medicine4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.ContentValues
import android.widget.TextView
import android.widget.Toast

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        // 홈으로 돌아가는 버튼
        val backHomeButton = findViewById<Button>(R.id.backButton)
        // 클릭시 홈으로 이동
        backHomeButton.setOnClickListener {
            val intent = Intent(applicationContext, NaviActivity::class.java)
            startActivity(intent)
        }

        // DBHelper 초기화
        dbHelper = DBHelper(this, "DRUG_INFO.db", null, 3)

        // TextView에 사용자 이메일 표시
        val emailTextView: TextView = findViewById(R.id.emailTextView)
        val userEmail = LoginActivity.loggedInUserEmail
        emailTextView.text = userEmail

        // 이메일을 기반으로 데이터베이스에서 사용자 정보를 가져오기
        val userInfoCursor = dbHelper.getUserInfoByEmail(userEmail.toString())

        if(userInfoCursor != null && userInfoCursor.moveToFirst()){
            // 사용자 정보를 나타내기
            val nameTextView: TextView = findViewById(R.id.editTextText)            // 이름
            val phoneNumberTextView: TextView = findViewById(R.id.editTextPhone)    // 전화번호
            val birthdateTextView: TextView = findViewById(R.id.editTextDate)       // 생년월일

            // "USERNAME" 열 처리
            val usernameIndex = userInfoCursor.getColumnIndex("USERNAME")
            if (usernameIndex != -1) {
                val name = userInfoCursor.getString(usernameIndex)
                nameTextView.text = "$name"
            } else {
                nameTextView.text = "이름 정보 없음"
            }

            // "PHONE" 열 처리
            val phoneIndex = userInfoCursor.getColumnIndex("PHONE")
            if (phoneIndex != -1) {
                val phoneNumber = userInfoCursor.getString(phoneIndex)
                phoneNumberTextView.text = "$phoneNumber"
            } else {
                phoneNumberTextView.text = "전화번호 정보 없음"
            }


            // "AGE" 열 처리
            val ageIndex = userInfoCursor.getColumnIndex("AGE")
            if (ageIndex != -1) {
                val birthdate = userInfoCursor.getString(ageIndex)
                birthdateTextView.text = "$birthdate"
            } else {
                birthdateTextView.text = "생년월일 정보 없음"
            }

        }

        // 저장 버튼
        val saveButton = findViewById<Button>(R.id.saveButton)
        // 저장 버튼 클릭 시 이벤트
        saveButton.setOnClickListener {
            // 사용자가 입력한 정보 가져오기
            val newName = findViewById<TextView>(R.id.editTextText).text.toString()
            val newPhoneNumber = findViewById<TextView>(R.id.editTextPhone).text.toString()
            val newBirthdate = findViewById<TextView>(R.id.editTextDate).text.toString()

            // DB 업데이트 메서드 호출
            updateUserInfo(newName, newPhoneNumber, newBirthdate)

            // 홈으로 돌아가도록 설정
            val intent = Intent(applicationContext, NaviActivity::class.java)
            startActivity(intent)

            // 토스트 메시지 출력
            Toast.makeText(
                applicationContext,
                "수정되었습니다.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun updateUserInfo(newName: String, newPhoneNumber: String, newBirthdate: String) {
        // 현재 로그인된 사용자의 이메일 가져오기
        val userEmail = LoginActivity.loggedInUserEmail

        // DB에서 현재 사용자의 정보 가져오기
        val userInfoCursor = dbHelper.getUserInfoByEmail(userEmail.toString())

        // 사용자 정보가 존재하면 업데이트
        if (userInfoCursor != null && userInfoCursor.moveToFirst()) {
            // 업데이트할 컬럼과 값을 ContentValues에 추가
            val values = ContentValues().apply {
                put("USERNAME", newName)
                put("PHONE", newPhoneNumber)
                put("AGE", newBirthdate)
            }

            // DB 업데이트
            dbHelper.writableDatabase.update("member", values, "EMAIL = ?", arrayOf(userEmail))

            // 커서 및 DB 닫기
            userInfoCursor.close()
            dbHelper.close()
        }
    }
}