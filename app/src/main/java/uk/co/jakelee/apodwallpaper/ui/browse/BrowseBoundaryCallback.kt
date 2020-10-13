package uk.co.jakelee.apodwallpaper.ui.browse

import androidx.paging.PagedList
import uk.co.jakelee.apodwallpaper.model.Apod

class BrowseBoundaryCallback(
    private val loadNewDataCallback: () -> Unit
) : PagedList.BoundaryCallback<Apod>() {

    override fun onZeroItemsLoaded() = loadNewDataCallback.invoke()

    override fun onItemAtFrontLoaded(itemAtFront: Apod) {}

    override fun onItemAtEndLoaded(itemAtEnd: Apod) = loadNewDataCallback.invoke()

}