package uk.co.jakelee.apodwallpaper.ui.browse.architecture

import uk.co.jakelee.apodwallpaper.app.architecture.IIntent
import uk.co.jakelee.apodwallpaper.model.Apod

sealed class BrowseIntent : IIntent {
    object FetchApods : BrowseIntent()
    class OpenApod(val apod: Apod) : BrowseIntent()
    object FollowingDirection: BrowseIntent()
}

