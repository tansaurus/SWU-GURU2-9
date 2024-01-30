package om.androidbook.medicine4

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import om.androidbook.medicine4.databinding.ActivityFindPasswordBinding



class FindPasswordActivity : AppCompatActivity() {
    lateinit var FindPasswordBinding: ActivityFindPasswordBinding
    private var doubleBackToExitPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FindPasswordBinding = ActivityFindPasswordBinding.inflate(layoutInflater)
        setContentView(FindPasswordBinding.root)


        val password = intent.getStringExtra("password")
        val passwordTextView: TextView = FindPasswordBinding.passwordTextView

        FindPasswordBinding.passwordTextView.text = "$password"

        // 사용자 닉네임 표시
        val name = intent.getStringExtra("name")
        val nameTextView: TextView = FindPasswordBinding.nameTextView

        if (name != null) {
            nameTextView.text = "$name 님의 비밀번호는"
        } else {
            nameTextView.text = "비밀번호는"
        }

        FindPasswordBinding.backLoginButton.setOnClickListener{
            val signupIntent = Intent(this@FindPasswordActivity, LoginActivity::class.java)
            startActivity(signupIntent)
        }

        onBackPressedDispatcher.addCallback(this@FindPasswordActivity) {
            if (doubleBackToExitPressedOnce) {
                // 앱 종료 로직을 추가할 수 있습니다.
                isEnabled = false // 콜백을 비활성화
                finishAffinity()
            } else {
                // 첫 번째 뒤로가기 버튼 클릭
                Toast.makeText(this@FindPasswordActivity, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                doubleBackToExitPressedOnce = true

                // 2초 동안 변수 초기화를 위한 핸들러
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
        }
    }

}