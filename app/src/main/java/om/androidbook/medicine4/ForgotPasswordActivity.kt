package om.androidbook.medicine4

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import om.androidbook.medicine4.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false
    lateinit var ForgotPasswordBinding: ActivityForgotPasswordBinding
    var DB:DBHelper?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        ForgotPasswordBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(ForgotPasswordBinding.root)
        DB = DBHelper(this, "DRUG_INFO", null, 3)

        ForgotPasswordBinding.checkPWButton!!.setOnClickListener{
            val email = ForgotPasswordBinding.emailAddressEditText!!.text.toString()
            val phonenumber = ForgotPasswordBinding.numberEditText!!.text.toString()
            if(email == "" || phonenumber == "")Toast.makeText(
                this@ForgotPasswordActivity,
                "회원정보를 전부 입력해주세요",
                Toast.LENGTH_SHORT
            ).show() else{
                val foundPW = DB!!.foundPW(email, phonenumber)
                if(foundPW == null){
                    Toast.makeText(this@ForgotPasswordActivity, "이메일로 가입된 정보가 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
                else{
                    val intent = Intent(applicationContext, FindPasswordActivity::class.java)
                    intent.putExtra("password", foundPW)
                    startActivity(intent)
                }
            }
        }
        onBackPressedDispatcher.addCallback(this@ForgotPasswordActivity) {
            if (doubleBackToExitPressedOnce) {
                // 앱 종료 로직을 추가할 수 있습니다.
                isEnabled = false // 콜백을 비활성화
                finishAffinity()
            } else {
                // 첫 번째 뒤로가기 버튼 클릭
                Toast.makeText(this@ForgotPasswordActivity, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                doubleBackToExitPressedOnce = true

                // 2초 동안 변수 초기화를 위한 핸들러
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
        }
    }
}