package uk.co.jakelee.apodwallpaper.app

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import java.io.File

class WallpaperHandler(
    private val manager: WallpaperManager
) {

    fun updateWallpaper(bitmap: Bitmap) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || manager.isSetWallpaperAllowed) {
            manager.setBitmap(bitmap)
        }
    }

    fun updateLockScreen(file: File) {
        if (canSetLockScreen()) {
            manager.setStream(file.inputStream(), null, true, WallpaperManager.FLAG_LOCK)
        } else {
            Log.i("LOCK", "Can't set lock screen before Android N!")
        }
    }

    companion object {
        fun canSetLockScreen() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }
}