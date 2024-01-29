package om.androidbook.medicine4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HomeAdapter(private val onItemClickListener: HomeAdapter.OnItemClickListener) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    private var doseList: List<dose> = emptyList()

    fun setData(userEmail: String?, dbHelper:DBHelper? = null) {
        if (userEmail != null && dbHelper != null) {
            doseList = dbHelper.getDoseList(userEmail)
            notifyDataSetChanged()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.home_medicine_list, parent, false)
        return HomeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HomeAdapter.HomeViewHolder, position: Int) {
        val dose = doseList[position]
        holder.medicineNameTextView.text = dose.name

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(dose)
        }
    }

    override fun getItemCount(): Int {
        return doseList.size
    }

    class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicineNameTextView: TextView = itemView.findViewById(R.id.medicineNameTextView)

    }
    interface OnItemClickListener {
        fun onItemClick(dose: dose)
    }

}


