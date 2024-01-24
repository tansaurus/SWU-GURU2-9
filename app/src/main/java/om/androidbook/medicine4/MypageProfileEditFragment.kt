package om.androidbook.medicine4

import android.content.ContentValues
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import android.widget.TextView
import android.widget.Toast


class MypageProfileEditFragment : Fragment() {
    private lateinit var dbHelper: DBHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_mypage_profile_edit, container, false)
        val mActivity = activity as NaviActivity



        val emailTextView: TextView = rootView.findViewById(R.id.emailTextView)
        val userEmail = LoginActivity.loggedInUserEmail
        emailTextView.text = userEmail

        dbHelper = DBHelper(requireContext(), "DRUG_INFO.db", null, 3)
        val userInfoCursor = dbHelper.getUserInfoByEmail(userEmail.toString())

        if(userInfoCursor != null && userInfoCursor.moveToFirst()){
            //사용자 정보 나타내기
            val nameTextView: TextView = rootView.findViewById(R.id.editTextText)
            val phoneNumberTextView: TextView = rootView.findViewById(R.id.editTextPhone)
            val birthdateTextView: TextView = rootView.findViewById(R.id.editTextDate)



// "USERNAME" 열 처리
            val usernameIndex = userInfoCursor.getColumnIndex("USERNAME")
            if (usernameIndex != -1) {
                val name = userInfoCursor.getString(usernameIndex)
                nameTextView.text = "$name"
            } else {
                nameTextView.text = "이름 정보 없음"
            }

// "PHONE" 열 처리
            val phoneIndex = userInfoCursor.getColumnIndex("PHONE")
            if (phoneIndex != -1) {
                val phoneNumber = userInfoCursor.getString(phoneIndex)
                phoneNumberTextView.text = "$phoneNumber"
            } else {
                phoneNumberTextView.text = "전화번호 정보 없음"
            }


// "AGE" 열 처리
            val ageIndex = userInfoCursor.getColumnIndex("AGE")
            if (ageIndex != -1) {
                val birthdate = userInfoCursor.getString(ageIndex)
                birthdateTextView.text = "$birthdate"
            } else {
                birthdateTextView.text = "생년월일 정보 없음"
            }

        }


        // 저장 버튼 클릭
        val saveButton = rootView.findViewById(R.id.saveButton) as AppCompatButton
        saveButton.setOnClickListener {
            // 사용자가 입력한 정보 가져오기
            val nameTextView: TextView = rootView.findViewById(R.id.editTextText)
            val phoneNumberTextView: TextView = rootView.findViewById(R.id.editTextPhone)
            val birthdateTextView: TextView = rootView.findViewById(R.id.editTextDate)
            val newName = nameTextView.text.toString()
            val newPhoneNumber = phoneNumberTextView.text.toString()
            val newBirthdate = birthdateTextView.text.toString()

            // DBHelper를 초기화
            dbHelper = DBHelper(requireContext(), "DRUG_INFO.db", null, 3)

            // DB 업데이트 메서드 호출
            updateUserInfo(newName, newPhoneNumber, newBirthdate)

            // NaviActivity에서 mypage 메뉴로 돌아가도록 설정
            mActivity.goBackToMypage()

            // 현재 Fragment를 제거
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().remove(this).commit()

            Toast.makeText(
                requireContext(),
                "수정되었습니다.",
                Toast.LENGTH_SHORT
            ).show()
        }

        return rootView
    }

    fun updateUserInfo(newName: String, newPhoneNumber: String, newBirthdate: String) {
        // 현재 로그인된 사용자의 이메일 가져오기
        val userEmail = LoginActivity.loggedInUserEmail

        // DB에서 현재 사용자의 정보 가져오기
        val userInfoCursor = dbHelper.getUserInfoByEmail(userEmail.toString())

        // 사용자 정보가 존재하면 업데이트
        if (userInfoCursor != null && userInfoCursor.moveToFirst()) {
            // 업데이트할 컬럼과 값을 ContentValues에 추가
            val values = ContentValues().apply {
                put("USERNAME", newName)
                put("PHONE", newPhoneNumber)
                put("AGE", newBirthdate)
            }

            // DB 업데이트
            dbHelper.writableDatabase.update("member", values, "EMAIL = ?", arrayOf(userEmail))

            // 커서 및 DB 닫기
            userInfoCursor.close()
            dbHelper.close()
        }
    }

    companion object {
        fun newInstance(): MypageProfileEditFragment {
            return MypageProfileEditFragment()
        }
    }
}



