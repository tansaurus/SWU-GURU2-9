package om.androidbook.medicine4


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
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
    private lateinit var scheduleManager: DBHelper
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var userEmail: String
    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0
    private var selectedDayOfMonth: Int = 0
    private var doubleBackToExitPressedOnce = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scheduleManager = DBHelper(requireContext(), "DRUG_INFO.db", null, 9)
        userEmail = LoginActivity().getLoggedInUserEmail(requireContext()) ?: ""

        calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val recyclerView = view.findViewById<RecyclerView>(R.id.schedulelistView)
        val contextEditText = view.findViewById<EditText>(R.id.contextEditText)
        val newEntryEditText = view.findViewById<EditText>(R.id.newEntryEditText)
        val registerButton = view.findViewById<Button>(R.id.registerButton)
        val DateView = view.findViewById<TextView>(R.id.DateView)
        val currentDate = getCurrentDate()
        DateView.text = currentDate
        val today = Calendar.getInstance()

        selectedYear = today.get(Calendar.YEAR)
        selectedMonth = today.get(Calendar.MONTH)
        selectedDayOfMonth = today.get(Calendar.DAY_OF_MONTH)



        scheduleAdapter = ScheduleAdapter(object : ScheduleAdapter.OnItemClickListener {
            override fun onItemClick(entry: ScheduleEntry) {
                showOptionsDialog(entry)
            }

            override fun onDeleteClick(entry: ScheduleEntry) {
                showDeleteDialog(entry)
            }

            override fun onUpdateClick(entry: ScheduleEntry) {
                showEditDialog(entry)
            }
        })

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
            scheduleAdapter.filterByDate(selectedDate, userEmail)
            DateView.text = ""
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (doubleBackToExitPressedOnce) {
                requireActivity().finishAffinity()
            } else {
                Toast.makeText(requireContext(), "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                doubleBackToExitPressedOnce = true

                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
        }

        registerButton.setOnClickListener {
            val today = Calendar.getInstance()
            selectedYear = today.get(Calendar.YEAR)
            selectedMonth = today.get(Calendar.MONTH)
            selectedDayOfMonth = today.get(Calendar.DAY_OF_MONTH)
            scheduleAdapter.filterByDate(currentDate, userEmail)

            val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                GregorianCalendar(selectedYear, selectedMonth, selectedDayOfMonth).time
            )
            val entry = contextEditText.text.toString()

            if (entry.isNotEmpty()) {
                val existingEntries = getEntriesForDateAndUser(userEmail, selectedDate)

                // 이미 해당 날짜에 일정이 존재하는지 확인
                if (!existingEntries.any { it.entries.contains(entry) }) {
                    // 중복되지 않은 경우에만 추가

                    scheduleManager.saveDiaryEntryForUser(userEmail, selectedDate, entry)
                    updateRecyclerView(selectedDate)
                    DateView.setText("")
                    Toast.makeText(requireContext(), "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "이미 같은 일정이 존재합니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "일정을 입력하세요.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun updateRecyclerView(selectedDate: String) {
        val entries = scheduleManager.getDiaryEntriesForUserAndDate(userEmail, selectedDate)
            .map { ScheduleEntry(userEmail, selectedDate, mutableListOf(it)) }
        scheduleAdapter.submitList(entries)
    }

    private fun getEntriesForDateAndUser(email: String, date: String): List<ScheduleEntry> {
        return scheduleManager.getDiaryEntriesForUserAndDate(email, date)
            .map { ScheduleEntry(email, date, mutableListOf(it)) }
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

        val input = EditText(requireContext())
        input.setText(entry.entries.joinToString("\n"))
        builder.setView(input)

        builder.setPositiveButton("확인") { dialog, _ ->
            val updatedEntry = input.text.toString()

            if (updatedEntry.isNotEmpty()) {
                val date = entry.date
                val originalEntry = entry.entries[0]
                scheduleManager.updateDiaryEntryForUser(userEmail, date, originalEntry, updatedEntry)
                updateRecyclerView(date)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun showDeleteDialog(entry: ScheduleEntry) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("일정 삭제")
        builder.setMessage("정말로 이 일정을 삭제하시겠습니까?")

        builder.setPositiveButton("확인") { dialog, _ ->
            val date = entry.date
            val entryToDelete = entry.entries[0]
            scheduleManager.deleteDiaryEntryForUserAndDate(userEmail, date, entryToDelete)
            updateRecyclerView(date)
            dialog.dismiss()
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        return dateFormat.format(calendar.time)
    }

}
