package om.androidbook.medicine4

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
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // 초기 생성 시 필요한 로직
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 데이터베이스 업그레이드 로직
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

    fun getDrugInfo(context: Context, drugName: String): Cursor? {
        try {
            val db = this.readableDatabase
            return db.query("drug_info", null, "DRUG_NAME LIKE ?", arrayOf("%$drugName%"), null, null, null) // 쿼리 업데이트
        } catch (e: Exception) {
            Toast.makeText(context, "Error while getting drug info: ${e.message}", Toast.LENGTH_LONG).show()
        }
        return null
    }

}

class DatabaseHelper(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object{
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "users"
        private const val COLUMN_USERNAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_BIRTH = "birth"
        private const val COLUMN_PHONENUMBER  = "phonenumber"

    }

    override fun onCreate(MyDB: SQLiteDatabase){
        MyDB.execSQL("create Table users(email textEmailAddress primary key, name TEXT, password password, birth datetime, phonenumber phone)")
    }

    override fun onUpgrade(MyDB: SQLiteDatabase, i: Int, i1: Int){
        MyDB.execSQL("drop Table if exists users")
    }

    /* override fun onCreate(MyDB: SQLiteDatabase?) {
         val createTableQuery = ("CREATE TABLE $TABLE_NAME("+
                 "$COLUMN_EMAIL textEmailAddress PRIMARY KEY, "+
                 "$COLUMN_USERNAME TEXT,"+
                 "$COLUMN_PHONENUMBER PHONE,"+
                 "$COLUMN_BIRTH DATETIME,"+
                 "$COLUMN_PASSWORD TEXT)")
         MyDB?.execSQL(createTableQuery)
     }

     override fun onUpgrade(MyDB: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
         val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
         MyDB?.execSQL(dropTableQuery)
         onCreate(MyDB)
     }*/

    fun insertData(name: String?, email: String?, password: String?, birth: String?, phonenumber: String?):Boolean{
        val MyDB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("name",name)
        contentValues.put("email",email)
        contentValues.put("password",password)
        contentValues.put("birth",birth)
        contentValues.put("phonenumber",phonenumber)
        val result = MyDB.insert("users", null, contentValues)
        return if (result == -1L) false else true
    }

    /*  fun insertData(name: String, email: String, password: String, birth: String, phonenumber: String): Long{
          val values = ContentValues().apply {
              put(COLUMN_USERNAME, name)
              put(COLUMN_PHONENUMBER, phonenumber)
              put(COLUMN_EMAIL, email)
              put(COLUMN_PASSWORD, password)
              put(COLUMN_BIRTH, birth)
          }
          val db = writableDatabase
          return db.insert(TABLE_NAME, null, values)
      }*/

    fun checkEM(email: String):Boolean{
        val MyDB = this.writableDatabase
        var res = true
        val cursor = MyDB.rawQuery("Select * from users where email=?", arrayOf(email))
        if(cursor.count <= 0) res = false
        return res
    }

    fun readUser(username: String, phonenumber: String, id: String, password: String): Boolean{
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ? AND $COLUMN_PHONENUMBER = ? AND $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(username, phonenumber, id, password)
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }
}