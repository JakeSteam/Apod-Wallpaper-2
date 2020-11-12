package uk.co.jakelee.apodwallpaper.app.storage

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/*
TODO:
 */
class FileSystemHelper(val context: Context) {

    private val relativePath = "${Environment.DIRECTORY_PICTURES}/APOD"

    //region Loading
    fun doesImageExist(name: String): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val file = File(context.getExternalFilesDir(relativePath), "$name.png")
            file.exists()
        } else {
            getUri("$name.png")?.let {
                return context.contentResolver.openInputStream(it) != null
            } ?: false
        }
    }

    private fun getUri(name: String): Uri? {
        try {
            val photoId: Long
            val photoUri: Uri = MediaStore.Images.Media.getContentUri("external")
            val projection = arrayOf(MediaStore.Images.ImageColumns._ID)
            val cursor: Cursor = context.contentResolver.query(
                photoUri,
                projection,
                MediaStore.Images.ImageColumns.DISPLAY_NAME + " LIKE ?",
                arrayOf(name),
                null
            )!!
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(projection[0])
            photoId = cursor.getLong(columnIndex)
            cursor.close()
            return Uri.parse("$photoUri/$photoId")
        } catch (e: Exception) {
            return null
        }
    }
    //endregion

    //region Saving
    fun saveImage(bitmap: Bitmap, name: String) = getOutputStream(name)?.let {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        it.close()
    }

    private fun getOutputStream(name: String): OutputStream? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getOutputStreamQPlus(context, name)
        } else {
            getOutputStreamPreQ(name)
        }
    }

    @Suppress("DEPRECATION")
    private fun getOutputStreamPreQ(name: String): FileOutputStream {
        val image = File(context.getExternalFilesDir(relativePath), "$name.png")
        return FileOutputStream(image)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getOutputStreamQPlus(context: Context, name: String): OutputStream? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$name.png")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
        }
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let {
            return context.contentResolver.openOutputStream(it)
        }
        return null
    }
    //endregion

}