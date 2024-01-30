package om.androidbook.medicine4

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import om.androidbook.medicine4.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var doubleBackToExitPressedOnce = false
    private var binding: FragmentHomeBinding? = null
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var userEmail: String
    private lateinit var dbHelper: DBHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using view binding
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DBHelper(requireContext(), "DRUG_INFO.db", null, 9)
        userEmail = LoginActivity().getLoggedInUserEmail(requireContext()) ?: ""
        recyclerView = binding!!.bookmarkListRecyclerView
        homeAdapter = HomeAdapter(object : HomeAdapter.OnItemClickListener {
            override fun onItemClick(dose: Dose) {
                // 리사이클러뷰 아이템 클릭 시 동작
                // 예: 별도의 화면으로 이동하거나 작업 수행
            }
        })
        val doseList = listOf(
            Dose("aa"))

        recyclerView.adapter = homeAdapter
        homeAdapter.setData(userEmail, dbHelper)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 복약 추가 버튼
        val addDailyMedicineButton = binding?.registerPageButton

        // 복약 추가 버튼 클릭 시 이벤트
        addDailyMedicineButton?.setOnClickListener {
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

        // 사용자의 이메일을 얻어와서 해당 사용자의 약 정보를 얻어온 후 어댑터에 설정
//        val userEmail = LoginActivity.loggedInUserEmail
        val dbHelper = DBHelper(requireContext(), "DRUG_INFO.db", null, 6)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        // Factory method와 다르게 별도의 parameters 없이 바로 인스턴스 생성
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}
