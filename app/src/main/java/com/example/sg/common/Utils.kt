package com.example.sg.common

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date

class Utils {
    companion object {
        fun uriToFile(context: Context, uri: Uri): File? {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                val file = createTempFile(context)
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(4 * 1024) // or other buffer size
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                    }
                    output.flush()
                    return file
                }
            }
            return null
        }

        @SuppressLint("SimpleDateFormat")
        private fun createTempFile(context: Context): File {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            )
        }
    }
}