package om.androidbook.medicine4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import om.androidbook.medicine4.databinding.ActivityLoginBinding

class loginActivity : AppCompatActivity() {

    val binding by lazy{ ActivityLoginBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intent = Intent(this, SignupActivity::class.java)
        binding.joinButton.setOnClickListener{startActivity(intent)}
    }
}