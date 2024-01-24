package om.androidbook.medicine4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class ScheduleRegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frament_addschedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)

        var backbutton = view.findViewById<Button>(R.id.backButton)

        backbutton.setOnClickListener {
            // 뒤로가기 버튼을 누르면 이전 페이지로 돌아가기
            requireActivity().onBackPressed()
        }
    }
}