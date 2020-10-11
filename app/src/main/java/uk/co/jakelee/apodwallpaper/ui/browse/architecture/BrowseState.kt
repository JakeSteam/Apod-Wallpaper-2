package uk.co.jakelee.apodwallpaper.ui.browse.architecture

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import uk.co.jakelee.apodwallpaper.app.architecture.IState
import uk.co.jakelee.apodwallpaper.model.Apod

data class BrowseState(
    val apods: LiveData<PagedList<Apod>>? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : IState