package uk.co.jakelee.apodwallpaper.ui.browse

import uk.co.jakelee.apodwallpaper.app.arch.IIntent

sealed class BrowseIntent : IIntent {
    object RefreshUsers : BrowseIntent()
    object FetchApods : BrowseIntent()
}

