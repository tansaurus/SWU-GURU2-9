package om.androidbook.medicine4


import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream

class ExternalStorageManager(private val basePath: String) {

    fun writeData(data1: Int, data2: String) {
        val path = "$basePath/sd_file.dat"
        val fos = FileOutputStream(path)
        val dos = DataOutputStream(fos)

        // 데이터를 쓴다.
        dos.writeInt(data1)
        dos.writeUTF(data2)
        dos.flush()
        dos.close()
    }

    fun readData(): Pair<Int, String>? {
        val path = "$basePath/sd_file.dat"
        val fis = FileInputStream(path)
        val dis = DataInputStream(fis)

        val data1 = dis.readInt()
        val data2 = dis.readUTF()
        dis.close()

        return Pair(data1, data2)
    }
}
