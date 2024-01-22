package om.androidbook.medicine4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import om.androidbook.medicine4.databinding.ActivityLoginBinding
import kotlin.math.log

class LoginActivity : AppCompatActivity() {
    lateinit var loginBinding: ActivityLoginBinding
    var DB:DBHelper?=null
    override fun onCreate(savedInstanceState: Bundle?){
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(loginBinding.root)
        DB = DBHelper(this, "DRUG_INFO", null, 3)

        loginBinding.loginButton!!.setOnClickListener{
            val email = loginBinding.emailAddressEditText!!.text.toString()
            val password = loginBinding.passwordEditText!!.text.toString()
            if (email == "" || password == "")Toast.makeText(
                this@LoginActivity,
                "회원정보를 전부 입력해주세요",
                 Toast.LENGTH_SHORT
            ).show() else{
                val checkEM = DB!!.checkEM(email)
                if(checkEM == true){
                    Toast.makeText(this@LoginActivity, "로그인 되었습니다.", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(applicationContext, NaviActivity::class.java)  //HomeActivity대신에 로그인 하고 나올 화면
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this@LoginActivity, "회원정보가 존재하지 않습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        loginBinding.joinButton.setOnClickListener{
            val signupIntent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(signupIntent)
        }


    }
}