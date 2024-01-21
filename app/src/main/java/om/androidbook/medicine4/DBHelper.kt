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
        private const val DATABASE_NAME = "drug_info.db"
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
            return db.query("drug_info", null, "약품명 LIKE ?", arrayOf("%$drugName%"), null, null, null)
        } catch (e: Exception) {
            Toast.makeText(context, "Error while getting drug info: ${e.message}", Toast.LENGTH_LONG).show()
        }
        return null
    }

}

class DatabaseHelper(private val context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object{
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "data"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PHONENUMBER  = "phonenumber"
        private const val COLUMN_USERID = "userid"
        private const val COLUMN_ID = "id"
        private const val COLUMN_PASSWORD = "password"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = ("CREATE TABLE $TABLE_NAME("+
                "$COLUMN_USERID INTEGER PRIMARY KEY AUTOINCRREMENT, "+
                "$COLUMN_USERNAME TEXT,"+
                "$COLUMN_PHONENUMBER PHONE,"+
                "$COLUMN_ID TEXT,"+
                "$COLUMN_PASSWORD TEXT)")
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertUser(username: String, phonenumber: String, id: String, password: String): Long{
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PHONENUMBER, phonenumber)
            put(COLUMN_ID, id)
            put(COLUMN_PASSWORD, password)

        }
        val db = writableDatabase
        return db.insert(TABLE_NAME, null, values)
    }

    fun readUser(username: String, phonenumber: String, id: String, password: String): Boolean{
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ? AND $COLUMN_PHONENUMBER = ? AND $COLUMN_ID = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(username, phonenumber, id, password)
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }
}