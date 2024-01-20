package om.androidbook.medicine4

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
