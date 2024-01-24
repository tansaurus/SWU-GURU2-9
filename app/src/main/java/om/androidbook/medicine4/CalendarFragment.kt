package om.androidbook.medicine4

import android.animation.Animator
import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CalendarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CalendarFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var ListLayout: FrameLayout
    private lateinit var ListView: RecyclerView
    private lateinit var moveButton: Button
    private var isExpanded = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_calendar, container, false)

        ListLayout = view.findViewById(R.id.listToMove)
        ListView = view.findViewById(R.id.schedulelistView)
        moveButton = view.findViewById(R.id.schedulelistButton)

        return view



    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CalendarFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CalendarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        moveButton.setOnClickListener {

            if (isExpanded) {
                collapseLayout()
            } else {
                expandLayout()
            }

        }


        val registerButton = view.findViewById<Button>(R.id.registerPageButton)
        registerButton.setOnClickListener {
            // 등록 버튼 클릭 시 RegistrationFragment로 전환
            val registrationFragment = ScheduleRegisterFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.mainFrameLayout, registrationFragment)
            transaction.addToBackStack(null)
            transaction.commit()



        }

    }
    private fun expandLayout() {

//        val animation = AnimatorInflater.loadAnimator(requireContext(), R.animator.slide_up_animation)
//        animation.setTarget(ListLayout)
//        animation.start()

        val animation = AnimatorInflater.loadAnimator(requireContext(), R.animator.slide_up_animation)


        // 내부 뷰 크기 고정
//        ListView.scaleY = 1.2f // 예시 값, 조절이 필요함
//
//        // 버튼 크기 고정
//        moveButton.scaleY = 1.0f // 예시 값, 조절이 필요함
//        val layoutParamsButton = moveButton.layoutParams
//        layoutParamsButton.height = 50 // 예시 값, 조절이 필요함
//        moveButton.layoutParams = layoutParamsButton
//
//        // 애니메이션 대상 설정
        animation.setTarget(ListLayout)

//         애니메이션 리스너 설정
        animation.addListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                // 애니메이션이 종료된 후에 추가적인 작업 수행
                // 예를 들어, 크기가 확장된 상태에서 버튼의 클릭 이벤트 등을 처리할 수 있습니다.

            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })




        // 애니메이션 실행
        animation.start()


        isExpanded = true

    }

    private fun collapseLayout() {
        val animation = AnimatorInflater.loadAnimator(requireContext(), R.animator.slide_down_animation)
        animation.setTarget(ListLayout)
        animation.start()


        isExpanded = false
    }

}