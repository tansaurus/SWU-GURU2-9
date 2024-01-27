package om.androidbook.medicine4

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import om.androidbook.medicine4.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using view binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 복약 추가 버튼
        val addDailyMedicineButton = binding.registerPageButton

        // 복약 추가 버튼 클릭 시 이벤트
        addDailyMedicineButton.setOnClickListener {
            // 복약 추가 화면으로 이동
            val intent = Intent(requireContext(), AddDailyMedicineActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        // Factory method와 다르게 별도의 parameters 없이 바로 인스턴스 생성
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}
