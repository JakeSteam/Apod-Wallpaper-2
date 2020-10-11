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
import java.util.*

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
            val pageRange = pageToDateRange(page++)
            Log.d("PAGES", "Start: ${pageRange.startDate}, end: ${pageRange.endDate}")
            val apods = apodApi.getApods(BuildConfig.AUTH_CODE, pageRange.startDate, pageRange.endDate)
            apodDao.insertAll(apods)
            loading = false
        }
    }

    data class ApodDateRange(val startDate: String, val endDate: String)

    // TODO: Move somewhere testable
    // TODO: Make testable
    private fun pageToDateRange(page: Int): ApodDateRange {
        val targetDate = Calendar.getInstance()
        targetDate.add(Calendar.MONTH, -page)
        val targetYear = targetDate.get(Calendar.YEAR)
        val targetMonth = targetDate.get(Calendar.MONTH) + 1
        val maxDay = if (page == 0) targetDate.get(Calendar.DAY_OF_MONTH) else targetDate.getActualMaximum(Calendar.DAY_OF_MONTH)
        return ApodDateRange(
            "$targetYear-$targetMonth-01",
            "$targetYear-$targetMonth-$maxDay"
        )
    }
}