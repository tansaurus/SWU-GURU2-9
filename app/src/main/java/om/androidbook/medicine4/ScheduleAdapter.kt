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
    private var selectedUserEmail: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.schedulelist, parent, false)
        return ScheduleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val entry = filteredEntries[position]
        holder.emailTextView.text = entry.email
        holder.dateTextView.text = entry.date
        holder.entriesTextView.text = entry.entries.joinToString("\n")

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(entry)
        }
    }

    override fun getItemCount(): Int {
        Log.d("ItemCountDebug", "Item Count: ${filteredEntries.size}")
        return filteredEntries.size
    }

    fun submitList(newEntries: List<ScheduleEntry>) {
        entries = newEntries
        updateFilteredEntries()
        notifyDataSetChanged()
    }

    fun filterByDate(selectedDate: String, userEmail: String) {
        this.selectedDate = selectedDate
        this.selectedUserEmail = userEmail
        Log.d("FilterDebug", "Selected Date: $selectedDate, User Email: $userEmail")
        updateFilteredEntries()
        notifyDataSetChanged()
    }
    private fun updateFilteredEntries() {
        filteredEntries = entries.filter { it.date == selectedDate && it.email == selectedUserEmail }.toMutableList()
        notifyDataSetChanged()
    }
    interface OnItemClickListener {
        fun onItemClick(entry: ScheduleEntry)
        fun onDeleteClick(entry: ScheduleEntry)
        fun onUpdateClick(entry: ScheduleEntry)
    }

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emailTextView: TextView = itemView.findViewById(R.id.ScheduleEmailView)
        val dateTextView: TextView = itemView.findViewById(R.id.ScheduleDateView)
        val entriesTextView: TextView = itemView.findViewById(R.id.ScheduleTextView)
    }
}
