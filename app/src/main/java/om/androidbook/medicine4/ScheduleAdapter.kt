package om.androidbook.medicine4

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScheduleAdapter(private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    private var entries: List<ScheduleEntry> = emptyList()
    private var filteredEntries: List<ScheduleEntry> = emptyList()
    private var selectedDate: String = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.schedulelist, parent, false)
        return ScheduleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {

        val entry = filteredEntries[position]

        holder.dateTextView.text = entry.date
        holder.entriesTextView.text = entry.entries.joinToString("\n")
//        holder.dateTextView.text = entry.date
//        holder.entriesTextView.text = entry.entries.joinToString("\n")

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(entry)
        }

        // 삭제 버튼 클릭 시
//        holder.deleteButton.setOnClickListener {
//            onItemClickListener.onDeleteClick(entry)
//        }
//
//        // 수정 버튼 클릭 시
//        holder.updateButton.setOnClickListener {
//            onItemClickListener.onUpdateClick(entry)
//        }
    }

    override fun getItemCount(): Int {
        return filteredEntries.size
    }

    fun submitList(newEntries: List<ScheduleEntry>) {
        entries = newEntries
        updateFilteredEntries()
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(entry: ScheduleEntry)
        fun onDeleteClick(entry: ScheduleEntry)
        fun onUpdateClick(entry: ScheduleEntry)
    }

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.ScheduleDateView)
        val entriesTextView: TextView = itemView.findViewById(R.id.ScheduleTextView)

    }
    fun filterByDate(selectedDate: String) {
        this.selectedDate = selectedDate
        updateFilteredEntries()
        notifyDataSetChanged()
        Log.d("ScheduleAdapter", "filterByDate - Selected Date: $selectedDate, Filtered Entries: $filteredEntries")
    }

    private fun updateFilteredEntries() {
        filteredEntries = entries.filter { it.date == selectedDate }
        Log.d("ScheduleAdapter", "updateFilteredEntries - Selected Date: $selectedDate, Filtered Entries: $filteredEntries")
    }
}
