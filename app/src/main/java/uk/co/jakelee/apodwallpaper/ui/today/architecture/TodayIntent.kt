package uk.co.jakelee.apodwallpaper.ui.today.architecture

import uk.co.jakelee.apodwallpaper.app.architecture.IIntent

sealed class TodayIntent : IIntent {
    object FetchLatest : TodayIntent()
}

