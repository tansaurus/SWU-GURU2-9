package om.androidbook.medicine4

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
import om.androidbook.medicine4.databinding.ActivityMainBinding




class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // ViewBinding을 사용하기 위한 바인딩 선언
    private var doubleBackToExitPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val apiKey = getString(R.string.google_api_key)


        // Log an error if apiKey is not set.
        if (apiKey.isEmpty() || apiKey == "DEFAULT_API_KEY") {
            finish()
            return
        }
        Places.initialize(applicationContext, apiKey)
        // 일정 시간 지연 이후 실행하기 위한 코드
        Handler(Looper.getMainLooper()).postDelayed({

            // 일정 시간이 지나면 MainActivity로 이동
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // 이전 키를 눌렀을 때 스플래스 스크린 화면으로 이동을 방지하기 위해
            // 이동한 다음 사용안함으로 finish 처리
            finish()

        }, 3000) // 시간 3초 이후 실행
        onBackPressedDispatcher.addCallback(this@MainActivity) {
            if (doubleBackToExitPressedOnce) {
                // 앱 종료 로직을 추가할 수 있습니다.
                isEnabled = false // 콜백을 비활성화
                finishAffinity()
            } else {
                // 첫 번째 뒤로가기 버튼 클릭
                Toast.makeText(this@MainActivity, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                doubleBackToExitPressedOnce = true

                // 2초 동안 변수 초기화를 위한 핸들러
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
        }

    }



}
