package uk.co.jakelee.apodwallpaper.ui.item.architecture

import androidx.navigation.NavDirections
import uk.co.jakelee.apodwallpaper.app.architecture.IState
import uk.co.jakelee.apodwallpaper.model.Apod
import uk.co.jakelee.apodwallpaper.model.ApodError

data class ItemState(
    val apod: Apod? = null,
    val isLoading: Boolean = false,
    val errorMessage: ApodError? = null,
    val pendingDirection: NavDirections? = null
) : IState