package om.androidbook.medicine4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import om.androidbook.medicine4.databinding.ActivityFindPasswordBinding



class FindPasswordActivity : AppCompatActivity() {
    lateinit var FindPasswordBinding: ActivityFindPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FindPasswordBinding = ActivityFindPasswordBinding.inflate(layoutInflater)
        setContentView(FindPasswordBinding.root)


        val password = intent.getStringExtra("password")
        val passwordTextView: TextView = FindPasswordBinding.passwordTextView

        FindPasswordBinding.passwordTextView.text = "$password"


        FindPasswordBinding.backLoginButton.setOnClickListener{
            val signupIntent = Intent(this@FindPasswordActivity, LoginActivity::class.java)
            startActivity(signupIntent)
        }


    }
}