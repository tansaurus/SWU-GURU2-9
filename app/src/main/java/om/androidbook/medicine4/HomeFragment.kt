package om.androidbook.medicine4

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import om.androidbook.medicine4.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var doubleBackToExitPressedOnce = false
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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (doubleBackToExitPressedOnce) {
                // 앱 종료 로직을 추가할 수 있습니다.
                requireActivity().finishAffinity()
            } else {
                // 첫 번째 뒤로가기 버튼 클릭
                Toast.makeText(requireContext(), "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                doubleBackToExitPressedOnce = true

                // 2초 동안 변수 초기화를 위한 핸들러
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
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
