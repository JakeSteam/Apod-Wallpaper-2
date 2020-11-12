package uk.co.jakelee.apodwallpaper.app.storage

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class FileSystemHelper(val context: Context) {

    private val relativePath = "${Environment.DIRECTORY_PICTURES}/APOD"

    fun saveImage(bitmap: Bitmap, name: String) {
        val outputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getOutputStreamQPlus(context, name)
        } else {
            getOutputStreamPreQ(name)
        }
        outputStream?.let {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            it.close()
        }
    }

    private fun getOutputStreamPreQ(name: String): FileOutputStream {
        val imagesDir: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val image = File(imagesDir, "$name.jpg")
        return FileOutputStream(image)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getOutputStreamQPlus(context: Context, name: String): OutputStream? {
        val resolver: ContentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$name.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let {
            return resolver.openOutputStream(it)
        }
        return null
    }

}