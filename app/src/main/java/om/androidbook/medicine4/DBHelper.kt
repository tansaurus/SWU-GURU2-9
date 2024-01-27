package om.androidbook.medicine4

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class DBHelper(
    context: Context,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    init {
        copyDatabase(context)
    }

    companion object {
        private const val DATABASE_NAME = "DRUG_INFO.db"  // 변경된 데이터베이스 파일 이름
        private const val DATABASE_VERSION = 4
    }

    override fun onCreate(db: SQLiteDatabase?) {
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }





    @Throws(IOException::class)
    private fun copyDatabase(context: Context) {
        val dbFile = context.getDatabasePath(DATABASE_NAME)

        if (!dbFile.exists()) {
            dbFile.parentFile?.mkdirs()
            val inputStream = context.assets.open(DATABASE_NAME)
            val outputStream = FileOutputStream(dbFile)

            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.flush()
            outputStream.close()
        }
    }

    fun insertDose(useremail: String, name: String, count: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("NAME", name)
            put("EMAIL", useremail)
            put("COUNT", count)
        }

        val result = db.insert("does_info", null, contentValues)
        db.close()

        // insert 메서드는 새로 추가된 행의 row ID를 반환하며, 오류 발생 시 -1을 반환합니다.
        return result != -1L
    }

    fun getDrugInfo(context: Context, drugName: String): Cursor? {
        try {
            val db = this.readableDatabase
            return db.query("drug_info", null, "DRUG_NAME LIKE ?", arrayOf("%$drugName%"), null, null, null) // 쿼리 업데이트
        } catch (e: Exception) {
            Toast.makeText(context, "Error while getting drug info: ${e.message}", Toast.LENGTH_LONG).show()
        }
        return null
    }

    fun addRecognizedDrug(drugName: String, therapeuticGroup: String, maxDailyDosage: String, ingredientName: String, contraindications: String) {
        val values = ContentValues().apply {
            put("DRUG_NAME", drugName)
            put("THERAPEUTIC_GROUP", therapeuticGroup)
            put("MAX_DAILY_DOSAGE", maxDailyDosage)
            put("INGREDIENT_NAME", ingredientName)
            put("CONTRAINDICATIONS", contraindications)
        }

        val db = this.writableDatabase

        // 트랜잭션 시작
        db.beginTransaction()

        try {
            db.insert("recognized_medicines", null, values)
            // 변경 사항 커밋
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // 오류 발생 시 처리
        } finally {
            // 트랜잭션 종료
            db.endTransaction()
            db.close()
        }
    }


    fun getRecognizedDrugs(): Cursor {
        val db = this.readableDatabase
        return db.query("recognized_medicines", null, null, null, null, null, null)
    }

    fun deleteRecognizedDrug(id: Int) {
        val db = this.writableDatabase
        db.delete("recognized_medicines", "id = ?", arrayOf(id.toString()))
        db.close()
    }




   fun checkEM(email: String): Boolean {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        var res = false

        try {
            cursor = db.rawQuery("SELECT * FROM member WHERE EMAIL = ?", arrayOf(email))
            res = cursor.moveToFirst() // 커서를 첫 번째 행으로 이동. 결과가 있으면 true, 없으면 false 반환
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close() // 커서가 null이 아닐 경우에만 close 호출
            db.close()
        }

        return res
    }


    fun foundPW(email: String, phonenumber: String): String? {
        // 데이터베이스 읽기 모드로 열기
        val db = this.readableDatabase

        // 커서 및 결과 변수 초기화
        var cursor: Cursor? = null
        var password: String? = null

        try {
            // SQL 쿼리 실행: 이메일과 전화번호가 일치하는 회원이 있는지 확인
            cursor = db.rawQuery("SELECT * FROM member WHERE EMAIL = ? AND PHONE = ?", arrayOf(email, phonenumber))

            // 커서의 첫 번째 행으로 이동하고 결과 확인
            if (cursor.moveToFirst()) {
                // PASSWORD 열의 인덱스를 가져오기
                val passwordColumnIndex = cursor.getColumnIndex("PASSWORD")

                // PASSWORD 열이 존재하면 해당 값을 읽어옴
                if (passwordColumnIndex != -1) {
                    password = cursor.getString(passwordColumnIndex)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // 커서가 null이 아닐 경우에만 close 호출
            cursor?.close()

            // 데이터베이스 닫기
            db.close()
        }

        // 결과 반환 (이메일과 전화번호에 대한 비밀번호 값 또는 null)
        return password
    }




    fun getUserInfoByEmail(email: String): Cursor? {
        val db = this.readableDatabase
        val columns = arrayOf("USERNAME", "PHONE", "AGE") // 필요한 열 목록

        try {
            return db.query("member", columns, "EMAIL = ?", arrayOf(email), null, null, null)
        } catch (e: Exception) {
            // 예외 처리
            e.printStackTrace()
        }
        return null
    }


    fun insertData(name: String, email: String, password: String, birth: String, phonenumber: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("USERNAME", name)
            put("EMAIL", email)
            put("PASSWORD", password)
            put("AGE", birth)
            put("PHONE", phonenumber)
            put("MANAGER", 1) // MANAGER 열에 기본값으로 1 설정
        }

        val result = db.insert("member", null, contentValues)
        db.close()

        // insert 메서드는 새로 추가된 행의 row ID를 반환하며, 오류 발생 시 -1을 반환합니다.
        return result != -1L
    }





    @SuppressLint("Range")
    fun getRecognizedDrugsList(): List<Medicine> {
        val medicinesList = mutableListOf<Medicine>()
        val db = this.readableDatabase
        val cursor = db.query("recognized_medicines", null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val drugName = cursor.getString(cursor.getColumnIndex("DRUG_NAME"))
                val therapeuticGroup = cursor.getString(cursor.getColumnIndex("THERAPEUTIC_GROUP"))
                val maxDailyDosage = cursor.getString(cursor.getColumnIndex("MAX_DAILY_DOSAGE"))
                val ingredientName = cursor.getString(cursor.getColumnIndex("INGREDIENT_NAME"))
                val contraindications = cursor.getString(cursor.getColumnIndex("CONTRAINDICATIONS"))

                val medicine = Medicine(id, drugName, therapeuticGroup, maxDailyDosage, ingredientName, contraindications)
                medicinesList.add(medicine)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return medicinesList
    }

    fun getRecentMedicines(): List<Medicine> {
        val list = mutableListOf<Medicine>()
        val db = this.readableDatabase
        // 예시 쿼리: 가장 최근 검색된 1개의 약 정보를 가져옵니다.
        val cursor = db.query("recognized_medicines", null, null, null, null, null, "search_time DESC", "1")

        if (cursor.moveToFirst()) {
            do {
                // 여기서 컬럼 인덱스 또는 이름을 사용하여 Medicine 객체 생성
                // 예: cursor.getString(cursor.getColumnIndex("DRUG_NAME"))
                // ...
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // 검색 기록을 불러오는 메소드
    @SuppressLint("Range")
    fun getSearchHistory(): List<String> {
        val searchHistory = mutableListOf<String>()
        val db = this.readableDatabase

        val cursor = db.query("recognized_medicines", arrayOf("DRUG_NAME"), null, null, null, null, "SEARCH_TIME DESC")

        while (cursor.moveToNext()) {
            val searchText = cursor.getString(cursor.getColumnIndex("DRUG_NAME"))
            searchHistory.add(searchText)
        }

        cursor.close()
        db.close()

        return searchHistory
    }

}
