package om.androidbook.medicine4

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton

class MypageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
    }
}