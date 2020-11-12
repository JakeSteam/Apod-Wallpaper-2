package uk.co.jakelee.apodwallpaper.app.storage

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class FileSystemHelper(val context: Context) {

    private val relativePath = "${Environment.DIRECTORY_PICTURES}/APOD"

    //region Loading
    fun doesImageExist(name: String): Boolean {
        getUri(name)?.let {
            return context.contentResolver.openInputStream(it) != null
        }
        return false
    }

    private fun getUri(name: String): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getUriQPlus(name)
        } else {
            getUriPreQ(name)
        }
    }

    private fun getUriPreQ(name: String): Uri? {
        return null
    }

    private fun getUriQPlus(displayName: String): Uri? {
        try {
            val photoId: Long
            val photoUri: Uri = MediaStore.Images.Media.getContentUri("external")
            val projection = arrayOf(MediaStore.Images.ImageColumns._ID)
            // TODO This will break if we have no matching item in the MediaStore.
            val cursor: Cursor = context.contentResolver.query(
                photoUri,
                projection,
                MediaStore.Images.ImageColumns.DISPLAY_NAME + " LIKE ?",
                arrayOf(displayName),
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
        val imagesDir: String = Environment.getExternalStoragePublicDirectory(relativePath).toString()
        val image = File(imagesDir, "$name.png")
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