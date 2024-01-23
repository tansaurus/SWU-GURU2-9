package om.androidbook.medicine4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchHistoryAdapter(private val searchHistory: List<String>) : RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_medicinelist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchText = searchHistory[position]
        holder.bind(searchText)
    }

    override fun getItemCount(): Int {
        return searchHistory.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val searchTextTextView: TextView = itemView.findViewById(R.id.serchView)

        fun bind(searchText: String) {
            searchTextTextView.text = searchText
        }
    }
}
