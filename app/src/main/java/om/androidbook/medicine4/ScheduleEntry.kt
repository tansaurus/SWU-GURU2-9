package om.androidbook.medicine4


data class ScheduleEntry(val email: String, val date: String, val entries: MutableList<String>)