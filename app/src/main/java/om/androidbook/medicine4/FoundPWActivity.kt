package om.androidbook.medicine4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import om.androidbook.medicine4.databinding.ActivityFoundPwactivityBinding

class FoundPWActivity : AppCompatActivity() {
    lateinit var FoundPWBinding: ActivityFoundPwactivityBinding
    var DB:DBHelper?=null
    override fun onCreate(savedInstanceState: Bundle?){
        FoundPWBinding = ActivityFoundPwactivityBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(FoundPWBinding.root)
        DB = DBHelper(this, "DRUG_INFO", null, 3)

        FoundPWBinding.비번찾기버튼!!.setOnClickListener{
            val email = FoundPWBinding.이메일칸아이디!!.text.toString()
            val phonenumber = FoundPWBinding.비번칸아이디!!.text.toString()
            if (email == "" || password == "") Toast.makeText(
                this@FoundPWActivity,
                "회원정보를 전부 입력해주세요",
                Toast.LENGTH_SHORT
            ).show() else{
                val foundPW = DB!!.foundPW(email, phonenumber)
                if(foundPW == null){
                    Toast.makeText(this@FoundPWActivity, "일치하지 않는 정보입니다.", Toast.LENGTH_SHORT)
                        .show()
                    
                }
                else{
                    val intent = Intent(applicationContext, 패스워드 알려주는 화면::class.java)
                    startActivity(intent)
                }
            }
        }


}