package uk.co.jakelee.apodwallpaper.ui.browse.architecture

import uk.co.jakelee.apodwallpaper.app.architecture.IIntent

sealed class BrowseIntent : IIntent {
    object RefreshApods : BrowseIntent()
    object FetchApods : BrowseIntent()
}

