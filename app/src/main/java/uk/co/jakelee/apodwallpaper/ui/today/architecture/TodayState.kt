package uk.co.jakelee.apodwallpaper.ui.today.architecture

import uk.co.jakelee.apodwallpaper.app.architecture.IState
import uk.co.jakelee.apodwallpaper.model.Apod

data class TodayState(
    val apod: Apod? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : IState