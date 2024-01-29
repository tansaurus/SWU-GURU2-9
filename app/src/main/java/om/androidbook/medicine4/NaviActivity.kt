package om.androidbook.medicine4


import android.os.Bundle
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