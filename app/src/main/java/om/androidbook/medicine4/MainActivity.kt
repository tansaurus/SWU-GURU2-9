package om.androidbook.medicine4;

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import om.androidbook.medicine4.LoginActivity
import om.androidbook.medicine4.databinding.ActivityMainBinding
import android.Manifest
import com.google.android.gms.maps.MapsInitializer
import com.google.android.libraries.places.api.Places


class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // ViewBinding을 사용하기 위한 바인딩 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Places.initialize(applicationContext, "GOOGLE_MAPS_API_KEY")

        // 일정 시간 지연 이후 실행하기 위한 코드
        Handler(Looper.getMainLooper()).postDelayed({

            // 일정 시간이 지나면 MainActivity로 이동
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // 이전 키를 눌렀을 때 스플래스 스크린 화면으로 이동을 방지하기 위해
            // 이동한 다음 사용안함으로 finish 처리
            finish()

        }, 500) // 시간 0.5초 이후 실행

    }

}
