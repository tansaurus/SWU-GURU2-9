package om.androidbook.medicine4


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import om.androidbook.medicine4.databinding.ActivityNaviBinding


private const val TAG_HOME = "home_fragment"
private const val TAG_PHARMACY = "pharmacy_fragment"
private const val TAG_UPLOAD = "upload_fragment"
private const val TAG_MANAGEMENT = "management_fragment"
private const val TAG_MYPAGE = "mypage_fragment"
private const val TAG_PROFILE_EDIT = "mypage_profile_edit_fragment"

class NaviActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNaviBinding
    var backPressedTime: Long = 0
//    override fun onBackPressed() {
//
////현재시간보다 크면 종료
//        if(backPressedTime + 3000 > System.currentTimeMillis()){
//
//            finishAffinity()//액티비티 종료
//        }else{
//            Toast.makeText(applicationContext, "한번 더 뒤로가기 버튼을 누르면 종료됩니다.",
//                Toast.LENGTH_SHORT).show()
//        }
////현재 시간 담기
//        backPressedTime = System.currentTimeMillis()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(TAG_HOME, HomeFragment())

        binding.navigationView.itemIconTintList = null

        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.pharmacyFragment -> setFragment(TAG_PHARMACY, PharmacyFragment())
                R.id.uploadFragment -> setFragment(TAG_UPLOAD, UploadFragment())
                R.id.managementFragment -> setFragment(TAG_MANAGEMENT, ScheduleRegisterFragment())
                R.id.mypageFragment -> setFragment(TAG_MYPAGE, MypageFragment())
            }
            true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null) {
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val home = manager.findFragmentByTag(TAG_HOME)
        val pharmacy = manager.findFragmentByTag(TAG_PHARMACY)
        val upload = manager.findFragmentByTag(TAG_UPLOAD)
        val management = manager.findFragmentByTag(TAG_MANAGEMENT)
        val myPage = manager.findFragmentByTag(TAG_MYPAGE)

        if (home != null) {
            fragTransaction.hide(home)
        }

        if (pharmacy != null) {
            fragTransaction.hide(pharmacy)
        }

        if (upload != null) {
            fragTransaction.hide(upload)
        }

        if (management != null) {
            fragTransaction.hide(management)
        }

        if (myPage != null) {
            fragTransaction.hide(myPage)
        }

        if (tag == TAG_HOME) {
            if (home != null) {
                fragTransaction.show(home)
            }
        } else if (tag == TAG_PHARMACY) {
            if (pharmacy != null) {
                fragTransaction.show(pharmacy)
            }
        } else if (tag == TAG_UPLOAD) {
            if (upload != null) {
                fragTransaction.show(upload)
            }
        } else if (tag == TAG_MANAGEMENT) {
            if (management != null) {
                fragTransaction.show(management)
            }
        } else if (tag == TAG_MYPAGE) {
            if (myPage != null) {
                fragTransaction.show(myPage)
            }
        }

        fragTransaction.commitAllowingStateLoss()
    }
}