package om.androidbook.medicine4

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ScheduleManagement(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "schedule_database"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // 데이터베이스 생성 시 실행되는 코드
        db?.execSQL("CREATE TABLE IF NOT EXISTS diary_entries (ID INTEGER PRIMARY KEY AUTOINCREMENT, DATE TEXT, ENTRY TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 데이터베이스 업그레이드 시 실행되는 코드
    }

    // 일정 저장
    fun saveDiaryEntry(date: String, entry: String): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put("DATE", date)
            put("ENTRY", entry)
        }

        val result = db.insert("diary_entries", null, contentValues)
        db.close()

        // insert 메서드는 새로 추가된 행의 row ID를 반환하며, 오류 발생 시 -1을 반환합니다.
        return result != -1L
    }

    // 날짜별로 일정 목록을 가져오는 메서드

    // 예시: 날짜별로 여러 개의 일정 저장하기
    fun saveMultipleEntriesForDate(date: String, entryList: List<String>): Boolean {
        var isSuccess = true

        for (entry in entryList) {
            if (!saveDiaryEntry(date, entry)) {
                isSuccess = false
                break
            }
        }

        return isSuccess
    }

    // 예시: 날짜별로 여러 개의 일정 가져오기

    fun deleteDiaryEntry(date: String, entry: String): Boolean {
        val db = writableDatabase
        val whereClause = "DATE = ? AND ENTRY = ?"
        val whereArgs = arrayOf(date, entry)

        val result = db.delete("diary_entries", whereClause, whereArgs)
        db.close()

        // delete 메서드는 삭제된 행의 개수를 반환하며, 오류 발생 시 0을 반환합니다.
        return result > 0
    }

    // 일정 수정
    fun updateDiaryEntry(date: String, oldEntry: String, newEntry: String): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put("ENTRY", newEntry)
        }

        val whereClause = "DATE = ? AND ENTRY = ?"
        val whereArgs = arrayOf(date, oldEntry)

        val result = db.update("diary_entries", contentValues, whereClause, whereArgs)
        db.close()

        // update 메서드는 수정된 행의 개수를 반환하며, 오류 발생 시 0을 반환합니다.
        return result > 0
    }

    fun getDiaryEntriesForDate(date: String): List<ScheduleEntry> {
        val entries = mutableListOf<ScheduleEntry>()
        val db = readableDatabase

        val selection = "DATE = ?"
        val selectionArgs = arrayOf(date)

        val cursor = db.query("diary_entries", arrayOf("DATE", "ENTRY"), selection, selectionArgs, null, null, null)

        while (cursor.moveToNext()) {
            val entryDate = cursor.getString(cursor.getColumnIndexOrThrow("DATE"))
            val entry = cursor.getString(cursor.getColumnIndexOrThrow("ENTRY"))

            val scheduleEntry = ScheduleEntry(entryDate, mutableListOf(entry))
            entries.add(scheduleEntry)
        }

        cursor.close()
        db.close()

        return entries
    }
    fun getDiaryEntries(date: String): List<String> {
        val entries = mutableListOf<String>()
        val db = readableDatabase

        val selection = "DATE = ?"
        val selectionArgs = arrayOf(date)

        val cursor = db.query("diary_entries", arrayOf("ENTRY"), selection, selectionArgs, null, null, null)

        while (cursor.moveToNext()) {
            val entry = cursor.getString(cursor.getColumnIndexOrThrow("ENTRY"))
            entries.add(entry)
        }

        cursor.close()
        db.close()

        return entries
    }

}