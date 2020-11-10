package uk.co.jakelee.apodwallpaper.app.storage

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

class FileSystemHelper {
    companion object {
        const val imagesDir = "apod"
    }
    private val suffix = ".png"

    fun saveImage(folder: File, image: Bitmap, date: String) {
        val filePath = File(folder, imagesDir)
        filePath.mkdirs()
        val stream = FileOutputStream("$filePath/$date$suffix")
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
    }

}