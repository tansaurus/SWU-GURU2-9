package om.androidbook.medicine4

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import om.androidbook.medicine4.databinding.ActivityLoginBinding
import java.security.MessageDigest
import kotlin.math.log

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var yourTextView: TextView


    var DB:DBHelper?=null
    companion object{
        var loggedInUserEmail: String? = null
        const val PERMISSIONS_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?){
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(loginBinding.root)
        DB = DBHelper(this, "DRUG_INFO", null, 3)
        checkPermissions()

        loginBinding.loginButton.setOnClickListener {
            val email = loginBinding.emailAddressEditText.text.toString()
            val password = loginBinding.passwordEditText.text.toString()
            if (email == "" || password == "") {
                Toast.makeText(this, "회원정보를 전부 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                val authId = DB!!.getAuthId(email, password)
                if (authId != -1) {
                    loggedInUserEmail = email
                    setLoggedIn(applicationContext, true)
                    isLoggedIn(applicationContext)
                    Log.d("LoginActivity", "로그인 성공: $loggedInUserEmail")
                    // Shared Preferences에 저장
                    val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putInt("AUTH_ID", authId)
                        putString("LoggedInUserEmail", email)
                        apply()
                    }
                    Toast.makeText(this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                    // 다음 화면으로 이동
                    val intent = Intent(applicationContext, NaviActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "회원정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        loginBinding.joinButton.setOnClickListener{
            val signupIntent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(signupIntent)
        }

        loginBinding.forgotButton.setOnClickListener{
            val signupIntent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(signupIntent)
        }



    }
    private fun checkPermissions() {
        val permissionsNeeded = ArrayList<String>()

        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA)
        }

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(),
                LoginActivity.PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LoginActivity.PERMISSIONS_REQUEST_CODE) {
            // 권한 요청 결과 처리
            // 예: 모든 권한이 부여되었는지 확인, 부여되지 않은 권한에 대한 처리 등
        }
    }

    // 로그인 상태를 저장
    private fun setLoggedIn(context: Context, isLoggedIn: Boolean) {
        val sharedPref = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("LoggedIn", isLoggedIn)
        editor.apply()
    }

    // 로그인 상태를 가져오기
    fun isLoggedIn(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("LoggedIn", false) // 기본값은 로그아웃 상태 (false)
    }



}