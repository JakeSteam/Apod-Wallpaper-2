package uk.co.jakelee.apodwallpaper.app.storage

import android.content.ContentResolver
import android.content.ContentValues
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import uk.co.jakelee.apodwallpaper.extensions.FolderInfo
import uk.co.jakelee.apodwallpaper.extensions.getFolderInfo
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class FileSystemHelper(
    private val contentResolver: ContentResolver,
    private val filesDir: File,
    useJpeg: Boolean
) {

    private val storageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    private val relativePath = "${Environment.DIRECTORY_PICTURES}/APOD"
    private val filetype = if (useJpeg) "jpeg" else "png"

    //region Summary
    fun getSavedImagesInfo(): FolderInfo {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            File(filesDir, "").getFolderInfo()
        } else {
            getMediaStoreInfo()
        }
    }

    private fun getMediaStoreInfo(): FolderInfo {
        try {
            contentResolver.query(
                storageUri,
                arrayOf(MediaStore.Images.Media.SIZE),
                null, null, null
            )?.let {
                var size = 0L
                while (it.count > 0 && it.moveToNext()) {
                    size += it.getLong(it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))
                }
                val count = it.count
                it.close()
                return FolderInfo(size, count)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return FolderInfo(0, 0)
    }
    //endregion

    //region Loading
    fun doesImageExist(date: String): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val file = File(filesDir, "$date.$filetype")
            file.exists()
        } else {
            getUri("$date.$filetype")?.let {
                return contentResolver.openInputStream(it) != null
            } ?: false
        }
    }

    private fun getUri(name: String): Uri? {
        try {
            contentResolver.query(
                storageUri,
                arrayOf(MediaStore.Images.ImageColumns._ID),
                MediaStore.Images.ImageColumns.DISPLAY_NAME + " LIKE ?",
                arrayOf(name),
                null
            )?.let {
                it.moveToFirst()
                val photoUri = storageUri
                val photoId = it.getLong(it.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID))
                it.close()
                return Uri.parse("$photoUri/$photoId")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    //endregion

    //region Saving
    fun saveImage(bitmap: Bitmap, name: String) = getOutputStream(name)?.let {
        val format = if (filetype == "png") Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
        bitmap.compress(format, 100, it)
        it.close()
    }

    private fun getOutputStream(name: String): OutputStream? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getOutputStreamQPlus(name)
        } else {
            getOutputStreamPreQ(name)
        }
    }

    @Suppress("DEPRECATION")
    private fun getOutputStreamPreQ(name: String): FileOutputStream {
        val image = File(filesDir, "$name.$filetype")
        return FileOutputStream(image)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getOutputStreamQPlus(name: String): OutputStream? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$name.$filetype")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/$filetype")
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
        }
        contentResolver.insert(storageUri, contentValues)?.let {
            return contentResolver.openOutputStream(it)
        }
        return null
    }
    //endregion

    //region Deleting
    fun deleteAllImages() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            File(filesDir, "").deleteRecursively()
        } else {
            contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null)
        }
    }
    //endregion

}