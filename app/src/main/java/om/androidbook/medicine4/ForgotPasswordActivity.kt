package om.androidbook.medicine4

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import om.androidbook.medicine4.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

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
    }
}