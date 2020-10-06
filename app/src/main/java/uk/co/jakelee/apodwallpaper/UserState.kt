package uk.co.jakelee.apodwallpaper

import uk.co.jakelee.apodwallpaper.app.arch.IState
import uk.co.jakelee.apodwallpaper.model.Apod

data class UserState(
    val apods: List<Apod> = listOf(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : IState