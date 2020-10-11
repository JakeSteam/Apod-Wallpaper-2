package uk.co.jakelee.apodwallpaper.ui.browse

import android.util.Log
import androidx.paging.PagedList
import androidx.paging.toLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.jakelee.apodwallpaper.BuildConfig
import uk.co.jakelee.apodwallpaper.app.database.ApodDao
import uk.co.jakelee.apodwallpaper.model.Apod
import uk.co.jakelee.apodwallpaper.model.ApodApi
import uk.co.jakelee.apodwallpaper.temphelpers.PagingHelper

class BrowseBoundaryCallback(
    private val apodDao: ApodDao,
    private val apodApi: ApodApi,
    private val viewModelScope: CoroutineScope
) : PagedList.BoundaryCallback<Apod>() {

    var page = 0
    var loading = false

    override fun onZeroItemsLoaded() {
        loadData()
    }

    override fun onItemAtFrontLoaded(itemAtFront: Apod) {
        // ???
    }

    override fun onItemAtEndLoaded(itemAtEnd: Apod) {
        loadData()
    }

    private fun loadData() {
        if (loading) return

        viewModelScope.launch(Dispatchers.IO) {
            loading = true

            Log.d("PAGES", "Page: $page")
            val pageRange = PagingHelper().pageToDateRange(page++)
            Log.d("PAGES", "Start: ${pageRange.startDate}, end: ${pageRange.endDate}")
            val apods = apodApi.getApods(BuildConfig.AUTH_CODE, pageRange.startDate, pageRange.endDate)
            apodDao.insertAll(apods)
            loading = false
        }
    }
}