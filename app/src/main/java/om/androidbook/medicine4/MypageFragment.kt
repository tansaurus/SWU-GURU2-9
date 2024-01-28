package om.androidbook.medicine4

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton

class MypageFragment : Fragment() {
    private lateinit var dbHelper: DBHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dbHelper = DBHelper(requireContext(), "DRUG_INFO.db", null, 5)
        // 이 프래그먼트에 대한 레이아웃을 인플레이트
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 프로필 수정 버튼
        val profileEditButton = view.findViewById<Button>(R.id.editProfileButton)

        // 프로필 수정 버튼 클릭 시 이벤트
        profileEditButton.setOnClickListener {
            // 프로필 수정 화면으로 이동
            val intent = Intent(requireContext(), ProfileEditActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.logoutButton).setOnClickListener {
            confirmLogout()

        }
    }
    private fun confirmLogout() {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle("로그아웃")
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("예") { dialog, which ->
                    // 로그아웃 로직을 여기에 추가
                    deleteAccount()
                }
                .setNegativeButton("아니오", null)
                .show()
        }
    }

    private fun deleteAccount() {
        val sharedPref = activity?.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val authId = sharedPref?.getInt("AUTH_ID", -1) ?: -1
        try {
            if (authId != -1) {
                clearCache(requireContext())
                setLoggedIn(false)
                clearLoginInfo()
                isLoggedIn(requireContext())
                navigateToMainFragment()

            }
        } catch (e: Exception) {
            Toast.makeText(context, "계정 삭제 중 오류 발생", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToMainFragment() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }
    private fun clearCache(context: Context) {
        try {
            val sharedPref = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.clear()
            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun setLoggedIn(isLoggedIn: Boolean) {
        // 로그인 상태를 SharedPreferences에 저장
        val sharedPref = activity?.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        editor?.putBoolean("LoggedIn", isLoggedIn)
        editor?.apply()
    }
    // SharedPreferences에서 로그인 정보 삭제
    private fun clearLoginInfo() {
        val sharedPref = activity?.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()

        // "LoggedIn" 키와 "LoggedInUserEmail" 키를 제거
        editor?.remove("LoggedIn")
        editor?.remove("AUTH_ID")
        editor?.remove("LoggedInUserEmail")

        // 변경 사항을 저장
        editor?.apply()
    }
    private fun isLoggedIn(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("LoggedIn", false) // 기본값은 로그아웃 상태 (false)
    }
}