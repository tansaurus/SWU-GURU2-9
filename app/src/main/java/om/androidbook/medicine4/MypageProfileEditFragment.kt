package om.androidbook.medicine4

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton

class MypageProfileEditFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_mypage_profile_edit, container, false)
        val mActivity = activity as NaviActivity

        // 저장 버튼 클릭
        val saveButton = rootView.findViewById(R.id.saveButton) as AppCompatButton
        saveButton.setOnClickListener {
            // NaviActivity에서 mypage 메뉴로 돌아가도록 설정
            mActivity.goBackToMypage()

            // 현재 Fragment를 제거
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().remove(this).commit()
        }

        return rootView
    }

    companion object {
        fun newInstance(): MypageProfileEditFragment {
            return MypageProfileEditFragment()
        }
    }
}