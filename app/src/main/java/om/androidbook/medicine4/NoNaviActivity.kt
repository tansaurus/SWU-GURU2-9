package om.androidbook.medicine4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NoNaviActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nonavi)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, ScheduleRegisterFragment())
        fragmentTransaction.commit()

        // addToBackStack을 사용하면 백 스택에 프래그먼트를 추가할 수 있습니다.
        // fragmentTransaction.addToBackStack(null)

        // 변경사항을 반영합니다.
        fragmentTransaction.commit()
    }
}