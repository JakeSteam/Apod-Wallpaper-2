package uk.co.jakelee.apodwallpaper.ui.item.architecture

import android.graphics.Bitmap
import uk.co.jakelee.apodwallpaper.app.architecture.IIntent
import uk.co.jakelee.apodwallpaper.model.Apod

sealed class ItemIntent : IIntent {
    object FetchLatest : ItemIntent()
    class OpenApod(val apod: Apod): ItemIntent()
    class OpenDate(val date: String): ItemIntent()
    object ExpandApod: ItemIntent()
    object PreviousApod: ItemIntent()
    object NextApod: ItemIntent()
    class SaveApod(val bitmap: Bitmap): ItemIntent()
    class SetWallpaper(val bitmap: Bitmap): ItemIntent()
    class SetLockScreen(val bitmap: Bitmap): ItemIntent()
    object FollowingDirection: ItemIntent()
}

