package om.androidbook.medicine4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import om.androidbook.medicine4.databinding.ActivitySignupBinding
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide

import kotlin.math.sign

class SignupActivity : AppCompatActivity() {
    lateinit var signupBinding: ActivitySignupBinding

    var DB:DBHelper?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        signupBinding = ActivitySignupBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(signupBinding.root)
        DB = DBHelper(this, "DRUG_INFO", null, 2)

        signupBinding.ImageplusButton.setOnClickListener { //버튼 이벤트

            val intent = Intent(Intent.ACTION_PICK) //갤러리 호출
            intent.type = "image/*"
            activityResult.launch(intent)
        }

        signupBinding.joinButton.setOnClickListener{
            val name = signupBinding.inputname.text.toString()
            val email = signupBinding.inputEM.text.toString()
            val password =signupBinding.inputPW.text.toString()
            val birth = signupBinding.inputBR.text.toString()
            val phonenumber = signupBinding.inputPN.text.toString()
            if(name =="" || email == "" || password == "" || birth == "" || phonenumber == "") {
                Toast.makeText(
                    this@SignupActivity,
                    "회원정보를 전부 입력해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
            }else {
                val checkEM = DB!!.checkEM(email)
                if (checkEM == false) {
                    val insert = DB!!.insertData(name, email, password, birth, phonenumber)
                    if (insert == true) {
                        Toast.makeText(
                            this@SignupActivity,
                            "가입되었습니다",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@SignupActivity, MapActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@SignupActivity,
                            "가입에 실패했습니다",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@SignupActivity,
                        "이미 가입된 회원입니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){

            if(it.resultCode == RESULT_OK && it.data!=null){

                val uri = it.data!!.data

                Glide.with(this)
                    .load(uri)
                    .into(signupBinding.profileAvatar)
            }
    }

}
