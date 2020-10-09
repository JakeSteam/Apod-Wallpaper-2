package uk.co.jakelee.apodwallpaper.ui.browse

import uk.co.jakelee.apodwallpaper.app.arch.IState
import uk.co.jakelee.apodwallpaper.model.Apod

data class BrowseState(
    val apods: List<Apod> = listOf(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : IState