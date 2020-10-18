package uk.co.jakelee.apodwallpaper.ui.item.architecture

import uk.co.jakelee.apodwallpaper.app.architecture.IIntent
import uk.co.jakelee.apodwallpaper.model.Apod

sealed class ItemIntent : IIntent {
    object FetchLatest : ItemIntent()
    class OpenApod(val apod: Apod): ItemIntent()
    class OpenDate(val date: String): ItemIntent()
    object ExpandApod: ItemIntent()
    object FollowingDirection: ItemIntent()
}

