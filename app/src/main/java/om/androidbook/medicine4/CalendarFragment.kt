package om.androidbook.medicine4

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import java.io.FileInputStream
import java.io.FileOutputStream


class CalendarFragment : Fragment() {

    private var fname: String = ""
    private var str: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 이 프래그먼트에 대한 레이아웃을 인플레이트합니다.
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 바인딩
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val save_Btn = view.findViewById<Button>(R.id.save_Btn)
        val contextEditText = view.findViewById<EditText>(R.id.contextEditText)
        val textView2 = view.findViewById<TextView>(R.id.textView2)
        val cha_Btn = view.findViewById<Button>(R.id.cha_Btn)
        val del_Btn = view.findViewById<Button>(R.id.del_Btn)

        // 달력 날짜 변경 리스너 설정
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            save_Btn.visibility = View.VISIBLE // 저장 버튼이 Visible
            contextEditText.visibility = View.VISIBLE // EditText가 Visible
            textView2.visibility = View.INVISIBLE // 저장된 일기 textView가 Invisible
            cha_Btn.visibility = View.INVISIBLE // 수정 Button이 Invisible
            del_Btn.visibility = View.INVISIBLE // 삭제 Button이 Invisible

// 날짜를 보여주는 텍스트에 해당 날짜를 넣는다.
            contextEditText.setText("") // EditText에 공백값 넣기

            checkedDay(year, month, dayOfMonth) // checkedDay 메소드 호출

        }

        // 저장 버튼 클릭 리스너 설정
        save_Btn.setOnClickListener {
            saveDiary(fname) // saveDiary 메소드 호출
            toast(fname + "데이터를 저장했습니다.") // 토스트 메세지
            str = contextEditText.getText().toString() // str 변수에 edittext내용을 toString
//형으로 저장
            textView2.text = "${str}" // textView에 str 출력
            save_Btn.visibility = View.INVISIBLE
            cha_Btn.visibility = View.VISIBLE
            del_Btn.visibility = View.VISIBLE
            contextEditText.visibility = View.INVISIBLE
            textView2.visibility = View.VISIBLE
        }

        val registerButton = view.findViewById<Button>(R.id.registerPageButton)
        registerButton.setOnClickListener {
            // 등록 버튼 클릭 시 RegistrationFragment로 전환
            val registrationFragment = SchedulRegisterFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.mainFrameLayout, registrationFragment)
            transaction.addToBackStack(null)
            transaction.commit()



        }


    }
    fun checkedDay(cYear: Int, cMonth: Int, cDay: Int) {
        fname = "$cYear-${cMonth + 1}-$cDay.txt"
        // 저장할 파일 이름 설정. 예: 2019-01-20.txt
        var fis: FileInputStream? = null

        try {
            fis = activity?.openFileInput(fname) // fname 파일 오픈!!

            val fileData = ByteArray(fis?.available() ?: 0)
            fis?.read(fileData)
            fis?.close()

            str = String(fileData)

            view?.findViewById<EditText>(R.id.contextEditText)?.visibility = View.INVISIBLE
            val textView2 = view?.findViewById<TextView>(R.id.textView2)
            textView2?.visibility = View.VISIBLE
            textView2?.text = str

            view?.findViewById<Button>(R.id.save_Btn)?.visibility = View.INVISIBLE
            val chaBtn = view?.findViewById<Button>(R.id.cha_Btn)
            val delBtn = view?.findViewById<Button>(R.id.del_Btn)
            chaBtn?.visibility = View.VISIBLE
            delBtn?.visibility = View.VISIBLE

            chaBtn?.setOnClickListener {
                view?.findViewById<EditText>(R.id.contextEditText)?.apply {
                    visibility = View.VISIBLE
                    setText(str)
                }
                textView2?.visibility = View.INVISIBLE
                view?.findViewById<Button>(R.id.save_Btn)?.visibility = View.VISIBLE
                chaBtn.visibility = View.INVISIBLE
                delBtn?.visibility = View.INVISIBLE
                textView2?.text = view?.findViewById<EditText>(R.id.contextEditText)?.text.toString()
            }


            delBtn?.setOnClickListener {
                textView2?.visibility = View.INVISIBLE
                view?.findViewById<EditText>(R.id.contextEditText)?.apply {
                    setText("")
                    visibility = View.VISIBLE
                }
                view?.findViewById<Button>(R.id.save_Btn)?.visibility = View.VISIBLE
                if (chaBtn != null) {
                    chaBtn.visibility = View.INVISIBLE
                }
                delBtn.visibility = View.INVISIBLE
                removeDiary(fname)
                toast("$fname 데이터를 삭제했습니다.")
            }

            if (textView2?.text.toString() == "") {
                textView2?.visibility = View.INVISIBLE
                view?.findViewById<Button>(R.id.save_Btn)?.visibility = View.VISIBLE
                chaBtn?.visibility = View.INVISIBLE
                delBtn?.visibility = View.INVISIBLE
                view?.findViewById<EditText>(R.id.contextEditText)?.visibility = View.VISIBLE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // 일기 저장 메서드
    fun saveDiary(readyDay: String) {
        var fos: FileOutputStream? = null

        try {
            // 액티비티의 컨텍스트를 사용하여 파일 출력 스트림을 엽니다.
            fos = activity?.openFileOutput(readyDay, Context.MODE_PRIVATE)
            val contextEditText = view?.findViewById<EditText>(R.id.contextEditText)
            val content: String = contextEditText?.text.toString()

            fos?.write(content.toByteArray())
            fos?.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // 일기 삭제 메서드
    @SuppressLint("WrongConstant")
    fun removeDiary(readyDay: String) {
        var fos: FileOutputStream? = null

        try {
            // 액티비티의 컨텍스트를 사용하여 파일 출력 스트림을 엽니다.
            fos = activity?.openFileOutput(readyDay, Context.MODE_PRIVATE)
            val content: String = "" // 삭제하려면 내용을 비웁니다.
            fos?.write(content.toByteArray())
            fos?.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // 토스트 메세지 출력용 함수 (예시)
    private fun toast(message: String) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show()
    }

    // 날짜 선택시 호출되는 메서드

}
