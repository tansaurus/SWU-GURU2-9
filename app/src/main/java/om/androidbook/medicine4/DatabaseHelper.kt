import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "DrugInfo.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS drug_info (id INTEGER PRIMARY KEY AUTOINCREMENT, product_name TEXT, efficacy_group TEXT)"
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS drug_info")
        onCreate(db)
    }

    // 데이터 삽입 메서드
    fun insertData(productName: String, efficacyGroup: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("product_name", productName)
            put("efficacy_group", efficacyGroup)
        }
        db.insert("drug_info", null, contentValues)
        db.close()
    }

    // 데이터 조회 메서드
    fun getData(productNameA: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM drug_info WHERE productNameA = ?", arrayOf(productNameA))
    }

    fun insertDataFromCSV(context: Context) {
        val db = this.writableDatabase

        // assets 폴더에서 CSV 파일 읽기
        context.assets.open("duplicationMedicine.csv").bufferedReader().useLines { lines ->
            lines.forEach { line ->
                val cols = line.split(",")
                if (cols.size >= 2) {
                    val contentValues = ContentValues().apply {
                        put("product_name", cols[0].trim())
                        put("efficacy_group", cols[1].trim())
                    }
                    db.insert("drug_info", null, contentValues)
                }
            }
        }
        db.close()
    }
    fun searchProduct(productName: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT efficacy_group FROM drug_info WHERE product_name LIKE ?", arrayOf("%$productName%"))

        var efficacyGroup: String? = null
        if (cursor.moveToFirst()) {
            efficacyGroup = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return efficacyGroup
    }
}
