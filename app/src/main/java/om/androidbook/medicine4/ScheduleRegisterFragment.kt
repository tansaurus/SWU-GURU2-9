package om.androidbook.medicine4


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale


class ScheduleRegisterFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var scheduleManager: ScheduleManagement
    private lateinit var scheduleAdapter: ScheduleAdapter
    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0
    private var selectedDayOfMonth: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment의 레이아웃을 inflate하여 반환
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ScheduleManager 초기화
        scheduleManager = ScheduleManagement(requireContext())

        calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val recyclerView = view.findViewById<RecyclerView>(R.id.schedulelistView)
        val contextEditText = view.findViewById<EditText>(R.id.contextEditText)
        val newEntryEditText = view.findViewById<EditText>(R.id.newEntryEditText)
        val registerButton = view.findViewById<Button>(R.id.registerButton)

        val DateView = view.findViewById<TextView>(R.id.DateView)
        val currentDate = getCurrentDate()
        DateView.text = currentDate

        // ScheduleAdapter 초기화
        scheduleAdapter = ScheduleAdapter(object : ScheduleAdapter.OnItemClickListener {
            override fun onItemClick(entry: ScheduleEntry) {
                // 아이템 클릭 시 수행할 동작
                showOptionsDialog(entry)
            }

            override fun onDeleteClick(entry: ScheduleEntry) {
                showDeleteDialog(entry)
            }

            override fun onUpdateClick(entry: ScheduleEntry) {
                showEditDialog(entry)
            }
        })
        scheduleManager = ScheduleManagement(requireContext())
        recyclerView.adapter = scheduleAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        updateRecyclerView(currentDate)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedYear = year
            selectedMonth = month
            selectedDayOfMonth = dayOfMonth
            val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                GregorianCalendar(year, month, dayOfMonth).time
            )
            updateRecyclerView(selectedDate)
            scheduleAdapter.filterByDate(selectedDate)
//            updateRecyclerView(selectedDate)
            DateView.text=""
        }



        // 등록 버튼을 눌렀을 때
        registerButton.setOnClickListener {
            val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                GregorianCalendar(selectedYear, selectedMonth, selectedDayOfMonth).time
            )
//            val selectedDate = contextEditText.text.toString() // 캘린더에서 선택된 날짜로 변경
            val entry = contextEditText.text.toString()




            // 해당 날짜의 일정 목록에 추가
            saveEntryToDiary(selectedDate, entry)
            updateRecyclerView(selectedDate)

            DateView.setText("")
            Toast.makeText(requireContext(), "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show()
        }




//        deleteButton.setOnClickListener {
//            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(calendarView.date))
//            val entry = contextEditText.text.toString()
//
//            if (deleteEntryFromDiary(formattedDate, entry)) {
//                // 삭제 성공
//                updateRecyclerView(formattedDate)
//                contextEditText.setText("")
//                Toast.makeText(requireContext(), "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
//            } else {
//                // 삭제 실패
//                Toast.makeText(requireContext(), "일정 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // 수정 버튼 클릭 시
//        modifyButton.setOnClickListener {
//            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(calendarView.date))
//            newEntryEditText.visibility = View.VISIBLE
//            val oldEntry = contextEditText.text.toString()
//            val newEntry = newEntryEditText.text.toString()
//
//            if (updateEntryInDiary(formattedDate, oldEntry, newEntry)) {
//                // 수정 성공
//                updateRecyclerView(formattedDate)
//                contextEditText.setText("")
//                newEntryEditText.setText("")
//                Toast.makeText(requireContext(), "일정이 수정되었습니다.", Toast.LENGTH_SHORT).show()
//            } else {
//                // 수정 실패
//                Toast.makeText(requireContext(), "일정 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun updateRecyclerView(selectedDate: String) {
        val entries = getEntriesForDate(selectedDate)
        scheduleAdapter.submitList(entries)
    }

    private fun saveEntryToDiary(date: String, entry: String) {
        scheduleManager.saveDiaryEntry(date, entry)
    }

    private fun deleteEntryFromDiary(date: String, entry: String): Boolean {
        return scheduleManager.deleteDiaryEntry(date, entry)
    }

    private fun updateEntryInDiary(date: String, oldEntry: String, newEntry: String): Boolean {
        return scheduleManager.updateDiaryEntry(date, oldEntry, newEntry)
    }

    private fun showOptionsDialog(entry: ScheduleEntry) {
        val options = arrayOf("수정", "삭제")

        AlertDialog.Builder(requireContext())
            .setTitle("일정 옵션")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditDialog(entry)
                    1 -> showDeleteDialog(entry)
                }
            }
            .show()
    }

    private fun showEditDialog(entry: ScheduleEntry) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("일정 수정")

        // 다이얼로그에 표시할 뷰 설정 (여기에서는 EditText를 사용)
        val input = EditText(requireContext())
        input.setText(entry.entries.joinToString("\n"))
        builder.setView(input)

        // 확인 버튼 설정
        builder.setPositiveButton("확인") { dialog, _ ->
            // 다이얼로그에서 입력한 내용 가져오기
            val updatedEntry = input.text.toString()

            // 업데이트할 내용이 비어있지 않은 경우에만 업데이트 수행
            if (updatedEntry.isNotEmpty()) {
                // ScheduleManager에서 업데이트 수행
                val date = entry.date
                val originalEntry = entry.entries[0]
                scheduleManager.updateDiaryEntry(date, originalEntry, updatedEntry)

                // RecyclerView 업데이트
                updateRecyclerView(date)
            }

            // 다이얼로그 닫기
            dialog.dismiss()
        }

        // 취소 버튼 설정
        builder.setNegativeButton("취소") { dialog, _ ->
            // 다이얼로그 닫기
            dialog.cancel()
        }

        // 다이얼로그 표시
        builder.show()
    }

    private fun showDeleteDialog(entry: ScheduleEntry) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("일정 삭제")
        builder.setMessage("정말로 이 일정을 삭제하시겠습니까?")

        // 확인 버튼 설정
        builder.setPositiveButton("확인") { dialog, _ ->
            // ScheduleManager에서 삭제 수행
            val date = entry.date
            val entryToDelete = entry.entries[0]
            scheduleManager.deleteDiaryEntry(date, entryToDelete)

            // RecyclerView 업데이트
            updateRecyclerView(date)

            // 다이얼로그 닫기
            dialog.dismiss()
        }

        // 취소 버튼 설정
        builder.setNegativeButton("취소") { dialog, _ ->
            // 다이얼로그 닫기
            dialog.cancel()
        }

        // 다이얼로그 표시
        builder.show()
    }
    private fun getEntriesForDate(date: String): List<ScheduleEntry> {
        return scheduleManager.getDiaryEntries(date)
            .map { ScheduleEntry(date, mutableListOf(it)) }
    }
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }


}
