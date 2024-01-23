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
        // recycler_view_item_layout.xml을 인플레이트
        val view = inflater.inflate(R.layout.home_medicine_list, parent, false)
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
        private val textView: TextView = view.findViewById(R.id.medicineNameTextView) // itemTextView ID 사용

        fun bind(medicine: Medicine, onDelete: (Medicine) -> Unit) {
            textView.text = medicine.name // 약 이름 표시
            // 필요한 경우, 다른 버튼에 대한 클릭 리스너도 여기에 추가
        }
    }
}
