package uk.co.jakelee.apodwallpaper.app

import android.app.WallpaperManager
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import java.io.File
import java.io.InputStream

class WallpaperHandler(
    private val manager: WallpaperManager
) {

    fun setWallpaper(inputStream: InputStream) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            manager.setBitmap(BitmapFactory.decodeStream(inputStream))
        } else if (manager.isSetWallpaperAllowed) {
            manager.setStream(inputStream, null, true, WallpaperManager.FLAG_SYSTEM)
        } else {
            Log.i("WALL", "Can't set wallpaper")
        }
    }

    fun setLockScreen(inputStream: InputStream) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            manager.setStream(inputStream, null, true, WallpaperManager.FLAG_LOCK)
        } else {
            Log.i("LOCK", "Can't set lock screen specifically before Android N!")
        }
    }

}