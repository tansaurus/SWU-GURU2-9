package om.androidbook.medicine4

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton

class MypageFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_mypage, container, false)
        val mActivity = activity as NaviActivity

        // 프로필 수정 버튼 클릭
        val editProfileButton = rootView.findViewById(R.id.editProfileButton) as AppCompatButton
        editProfileButton.setOnClickListener {
            mActivity.changeMyPageFragment("TAG_PROFILE_EDIT", MypageProfileEditFragment())
        }

        // 즐겨찾기 버튼 클릭
        val bookmarkButton = rootView.findViewById(R.id.bookmarkButton) as AppCompatButton
        bookmarkButton.setOnClickListener{
            mActivity.changeMyPageFragment("TAG_BOOKMARK", MypageBookmarkFragment())
        }
        return rootView
    }
}