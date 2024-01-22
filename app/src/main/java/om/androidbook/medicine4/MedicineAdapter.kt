package om.androidbook.medicine4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// MedicineAdapter 클래스
class MedicineAdapter(private val medicines: MutableList<Medicine>, private val onDelete: (Medicine) -> Unit) : RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.activity_medicinelist, parent, false)
        return MedicineViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicine = medicines[position]
        holder.bind(medicine, onDelete)
    }

    override fun getItemCount(): Int = medicines.size

    // clear 메서드 추가
    fun clear() {
        medicines.clear()
        notifyDataSetChanged()
    }

    // addAll 메서드 추가
    fun addAll(newMedicines: List<Medicine>) {
        medicines.addAll(newMedicines)
        notifyDataSetChanged()
    }

    class MedicineViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.mediclinelistView)

        fun bind(medicine: Medicine, onDelete: (Medicine) -> Unit) {
            textView.text = medicine.name
            view.setOnClickListener { onDelete(medicine) }
        }
    }
}
