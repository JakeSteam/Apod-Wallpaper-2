package uk.co.jakelee.apodwallpaper.app.storage

import android.content.ContentValues
import android.content.Context
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


class FileSystemHelper(val context: Context) {

    private val relativePath = "${Environment.DIRECTORY_PICTURES}/APOD"

    //region Summary
    fun getSavedImagesInfo(): FolderInfo {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            File(context.getExternalFilesDir(relativePath), "").getFolderInfo()
        } else {
            getMediaStoreInfo()
        }
    }
    //endregion

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
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.ImageColumns._ID),
                MediaStore.Images.ImageColumns.DISPLAY_NAME + " LIKE ?",
                arrayOf(name),
                null
            )?.let {
                it.moveToFirst()
                val photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val photoId = it.getLong(it.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID))
                it.close()
                return Uri.parse("$photoUri/$photoId")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getMediaStoreInfo(): FolderInfo {
        try {
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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

    //region Deleting
    fun deleteAllImages() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            File(context.getExternalFilesDir(relativePath), "").deleteRecursively()
        } else {
            context.contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null)
        }
    }
    //endregion

}