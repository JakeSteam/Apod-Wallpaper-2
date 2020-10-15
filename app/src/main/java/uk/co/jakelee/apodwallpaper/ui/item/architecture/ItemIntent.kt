package uk.co.jakelee.apodwallpaper.ui.item.architecture

import uk.co.jakelee.apodwallpaper.app.architecture.IIntent

sealed class ItemIntent : IIntent {
    object FetchLatest : ItemIntent()
}

