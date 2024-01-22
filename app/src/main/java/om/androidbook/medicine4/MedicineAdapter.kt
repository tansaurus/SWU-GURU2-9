package om.androidbook.medicine4

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Medicine 모델 클래스
data class Medicine(
    val id: Int,
    val name: String,
    val group: String,
    val maxDailyDosage: String,
    val ingredientName: String,
    val contraindications: String
)

// MedicineAdapter 클래스
class MedicineAdapter(private val medicines: List<Medicine>, private val onDelete: (Medicine) -> Unit) : RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        // 간단한 TextView를 사용하여 ViewHolder를 생성합니다.
        val textView = TextView(parent.context)
        textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return MedicineViewHolder(textView)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicine = medicines[position]
        holder.bind(medicine, onDelete)
    }

    override fun getItemCount(): Int = medicines.size

    class MedicineViewHolder(private val textView: TextView) : RecyclerView.ViewHolder(textView) {
        fun bind(medicine: Medicine, onDelete: (Medicine) -> Unit) {
            textView.text = medicine.name
            // 여기에 클릭 리스너를 추가하여 약 정보 삭제 등의 기능을 구현할 수 있습니다.
            textView.setOnClickListener { onDelete(medicine) }
        }
    }
}