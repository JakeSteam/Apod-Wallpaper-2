package uk.co.jakelee.apodwallpaper

import uk.co.jakelee.apodwallpaper.app.arch.IIntent

sealed class UserIntent : IIntent {
    object RefreshUsers : UserIntent()
    object FetchUsers : UserIntent()
}

