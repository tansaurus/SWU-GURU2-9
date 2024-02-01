package om.androidbook.medicine4

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import java.io.FileOutputStream
import java.io.IOException

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
        private const val DATABASE_NAME = "DRUG_INFO.db"
        private const val DATABASE_VERSION = 11
    }

    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {
            db.execSQL("""
        CREATE TABLE IF NOT EXISTS Authority (
          AUTH_ID INTEGER NOT NULL UNIQUE,
          AUTH_TYPE TEXT NOT NULL,
          AUTH_ROLE TEXT NOT NULL,
          PRIMARY KEY (AUTH_ID)
        );
    """)

            // Cart 테이블 생성
            db.execSQL("""
        CREATE TABLE IF NOT EXISTS Cart (
          AUTH_ID TEXT NOT NULL UNIQUE,
          CART_QUANTITY TEXT NOT NULL,
          PRIMARY KEY (AUTH_ID),
          FOREIGN KEY (AUTH_ID) REFERENCES member(AUTH_ID)
        );
    """)

            // OrderTable 테이블 생성
            db.execSQL("""
        CREATE TABLE IF NOT EXISTS OrderTable (
          AUTH_ID TEXT NOT NULL,
          DRUG_DATE DATE NOT NULL,
          ORDER_COMMENT TEXT,
          FOREIGN KEY (AUTH_ID) REFERENCES member(AUTH_ID)
        );
    """)

            // DrugSearch 테이블 생성
            db.execSQL("""
        CREATE TABLE IF NOT EXISTS DrugSearch (
          SEARCH_ID INTEGER NOT NULL UNIQUE,
          AUTH_ID TEXT NOT NULL,
          DRUG_NAME TEXT NOT NULL,
          PRIMARY KEY (SEARCH_ID),
          FOREIGN KEY (AUTH_ID) REFERENCES member(AUTH_ID),
          FOREIGN KEY (DRUG_NAME) REFERENCES drug_info(DRUG_NAME)
        );
    """)

            // drug_info 테이블 생성
            db.execSQL("""
        CREATE TABLE IF NOT EXISTS drug_info (
          DRUG_NAME TEXT,
          THERAPEUTIC_GROUP TEXT,
          MAX_DAILY_DOSAGE TEXT,
          INGREDIENT_NAME TEXT,
          CONTRAINDICATIONS TEXT
        );
    """)

            // member 테이블 생성
            db.execSQL("""
        CREATE TABLE IF NOT EXISTS member (
          EMAIL TEXT PRIMARY KEY,
          AUTH_ID TEXT,
          PASSWORD TEXT,
          USERNAME TEXT,
          AGE STRING,
          GENDER TEXT,
          PHONE TEXT,
          ENROLL_DATE TEXT,
          LAST_UPDATE TEXT,
          MANAGER INTEGER
        );
    """)

            // dose_info 테이블 생성
            db.execSQL("""
        CREATE TABLE IF NOT EXISTS dose_info(
          AUTH_ID TEXT NOT NULL,
          NAME TEXT NOT NULL PRIMARY KEY,
          COUNT INTEGER NOT NULL,
          EMAIL TEXT NOT NULL,
          FOREIGN KEY (EMAIL) REFERENCES member(EMAIL)
        );
    """)

            db.execSQL("""
        CREATE TABLE IF NOT EXISTS diary_entries(
            ID INTEGER PRIMARY KEY AUTOINCREMENT,
            EMAIL TEXT NOT NULL,
            DATE TEXT NOT NULL,
            ENTRY TEXT NOT NULL,
            FOREIGN KEY (EMAIL) REFERENCES member(EMAIL)
        );
""")

        }
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 기존 테이블 삭제
        db.execSQL("DROP TABLE IF EXISTS Authority")
        db.execSQL("DROP TABLE IF EXISTS Cart")
        db.execSQL("DROP TABLE IF EXISTS OrderTable")
        db.execSQL("DROP TABLE IF EXISTS DrugSearch")
        db.execSQL("DROP TABLE IF EXISTS drug_info")
        db.execSQL("DROP TABLE IF EXISTS member")
        db.execSQL("DROP TABLE IF EXISTS dose_info")
        db.execSQL("DROP TABLE IF EXISTS diary_entries")

        // 새로운 테이블 생성
        onCreate(db)

        db.execSQL("DROP TABLE IF EXISTS Cart")
        db.execSQL("DROP TABLE IF EXISTS Authority")
        db.execSQL("DROP TABLE IF EXISTS DrugSearch")
        db.execSQL("DROP TABLE IF EXISTS OrderTable")

        db.execSQL("DROP TABLE IF EXISTS member")
        db.execSQL("""
        CREATE TABLE IF NOT EXISTS member(
            EMAIL TEXT PRIMARY KEY NOT NULL,
            AUTH_ID TEXT NOT NULL,
            PASSWORD TEXT NOT NULL,
            USERNAME TEXT NOT NULL,
            AGE TEXT NOT NULL,
            PHONE TEXT NOT NULL
        );
""")
        db.execSQL("""INSERT INTO member (EMAIL, AUTH_ID, PASSWORD, USERNAME, AGE, PHONE) 
            VALUES ("tansaurus5001@swu.ac.kr", "swu", "swuswu9", "김슈니", "011005", "01011112222")""")
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
            put("AUTH_ID", getAuthIdForEmail(useremail)) // 사용자 이메일에 해당하는 AUTH_ID를 설정
            put("NAME", name)
            put("EMAIL", useremail)
            put("COUNT", count)
        }

        val result = db.insert("dose_info", null, contentValues)
        db.close()

        return result != -1L
    }

    fun getDoseList(userEmail: String): List<Dose> {
        val doseList = mutableListOf<Dose>()
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM dose_info WHERE EMAIL = ?", arrayOf(userEmail))

        if (cursor.moveToFirst()) {
            val nameColumnIndex = cursor.getColumnIndex("NAME")


            do {
                val name = cursor.getString(nameColumnIndex)
                val dose = Dose(name)
                doseList.add(dose)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return doseList
    }

    // ... (이전 코드 유지)

    fun deleteDose(name: String, userEmail: String): Boolean {
        val db = this.writableDatabase
        val whereClause = "NAME = ? AND EMAIL = ?"
        val whereArgs = arrayOf(name, userEmail)

        val result = db.delete("dose_info", whereClause, whereArgs)
        db.close()

        return result > 0
    }


    // 사용자 이메일에 해당하는 AUTH_ID를 찾는 메소드
    fun getAuthIdForEmail(useremail: String): Int {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            // member 테이블에서 주어진 이메일에 해당하는 AUTH_ID 검색
            cursor = db.rawQuery("SELECT AUTH_ID FROM member WHERE EMAIL = ?", arrayOf(useremail))
            if (cursor != null && cursor.moveToFirst()) {
                // AUTH_ID 열의 인덱스를 찾고, 해당 값 반환
                val authIdIndex = cursor.getColumnIndex("AUTH_ID")
                if (authIdIndex != -1) {
                    return cursor.getInt(authIdIndex)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close() // 커서가 null이 아닐 경우에만 close 호출
        }
        return -1 // 사용자를 찾을 수 없는 경우 또는 오류 발생 시 -1 반환
    }



    fun getDrugInfo(context: Context, drugName: String): Cursor? {
        try {
            val db = this.readableDatabase
            val query = "SELECT * FROM drug_info WHERE DRUG_NAME LIKE ?"
            Log.d("DBHelper", "Executing query: $query with drugName: $drugName")
            return db.query("drug_info", null, "DRUG_NAME LIKE ?", arrayOf("%$drugName%"), null, null, null)
        } catch (e: Exception) {
            Log.e("DBHelper", "Error while getting drug info", e)
            Toast.makeText(context, "Error while getting drug info: ${e.message}", Toast.LENGTH_LONG).show()
        }
        return null
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
    fun getAuthId(email: String, password: String): Int {
        // 이메일과 비밀번호를 사용하여 사용자를 찾고 AUTH_ID를 반환하는 로직
        val db = this.readableDatabase
        val query = "SELECT AUTH_ID FROM member WHERE email = ? AND password = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))

        if (cursor.moveToFirst()) {
            val authId = cursor.getInt(cursor.getColumnIndex("AUTH_ID"))
            cursor.close()
            return authId
        } else {
            cursor.close()
            return -1 // 사용자를 찾을 수 없는 경우
        }
    }
    //일정 저장
    fun saveDiaryEntryForUser(email: String, date: String, entry: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("EMAIL", email)
            put("DATE", date)
            put("ENTRY", entry)
        }

        return db.insert("diary_entries", null, values)
    }
    fun deleteDiaryEntryForUserAndDate(email: String, date: String, entry: String): Boolean {
        val db = writableDatabase
        val whereClause = "EMAIL = ? AND DATE = ? AND ENTRY = ?"
        val whereArgs = arrayOf(email, date, entry)

        val result = db.delete("diary_entries", whereClause, whereArgs)
        db.close()

        // delete 메서드는 삭제된 행의 개수를 반환하며, 오류 발생 시 0을 반환합니다.
        return result > 0
    }
    fun updateDiaryEntryForUser(email: String, date: String, originalEntry: String, updatedEntry: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("ENTRY", updatedEntry)
        }

        val whereClause = "EMAIL = ? AND DATE = ? AND ENTRY = ?"
        val whereArgs = arrayOf(email, date, originalEntry)

        val updatedRows = db.update("diary_entries", values, whereClause, whereArgs)
        db.close()

        return updatedRows > 0
    }
    fun getDiaryEntriesForUserAndDate(email: String, date: String): List<String> {
        val db = readableDatabase
        val entries = mutableListOf<String>()

        val selection = "EMAIL = ? AND DATE = ?"
        val selectionArgs = arrayOf(email, date)

        val cursor = db.query(
            "diary_entries",
            arrayOf("ENTRY"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val entry = cursor.getString(cursor.getColumnIndexOrThrow("ENTRY"))
            entries.add(entry)
        }

        cursor.close()
        db.close()
        Log.d("DBHelper", "getDiaryEntriesForUserAndDate: email=$email, date=$date, entries=$entries")
        return entries
    }


}