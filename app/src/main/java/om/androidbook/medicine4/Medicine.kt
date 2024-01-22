package om.androidbook.medicine4

data class Medicine(
    val id: Int,
    val name: String,
    val group: String,
    val maxDailyDosage: String,
    val ingredientName: String,
    val contraindications: String
)
