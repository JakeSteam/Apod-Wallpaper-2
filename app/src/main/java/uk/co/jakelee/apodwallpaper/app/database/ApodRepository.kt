package uk.co.jakelee.apodwallpaper.app.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.jakelee.apodwallpaper.BuildConfig
import uk.co.jakelee.apodwallpaper.model.Apod
import uk.co.jakelee.apodwallpaper.model.ApodApi
import uk.co.jakelee.apodwallpaper.ui.browse.BrowseBoundaryCallback
import java.util.*

class ApodRepository(
    private val apodDao: ApodDao,
    private val apodApi: ApodApi
) {

    private var currentPage = 0
    private val browsePageConfig = PagedList.Config.Builder()
        .setPageSize(9)
        .setPrefetchDistance(36)
        .setEnablePlaceholders(true)
        .build()

    data class ApodDateRange(val startDate: String, val endDate: String)

    // TODO: Use simpledateformat to display date
    // TODO: Make testable
    private fun pageToDateRange(page: Int): ApodDateRange {
        val targetDate = Calendar.getInstance()
        targetDate.add(Calendar.MONTH, -page)
        val targetYear = targetDate.get(Calendar.YEAR)
        val targetMonth = targetDate.get(Calendar.MONTH) + 1
        val maxDay = if (page == 0) targetDate.get(Calendar.DAY_OF_MONTH) else targetDate.getActualMaximum(
            Calendar.DAY_OF_MONTH)
        return ApodDateRange(
            "$targetYear-$targetMonth-01",
            "$targetYear-$targetMonth-$maxDay"
        )
    }

    data class ApodResponse(val results: LiveData<PagedList<Apod>>?, val error: String?)

    suspend fun getApods(scope: CoroutineScope): LiveData<PagedList<Apod>> {
        Log.d("PAGES", "Starting try")
        val callback: () -> Unit = {
            scope.launch(Dispatchers.IO) {
                Log.d("PAGES", "Page: $currentPage")
                val pageRange = pageToDateRange(currentPage++)
                Log.d("PAGES", "Start: ${pageRange.startDate}, end: ${pageRange.endDate}")

                val apods = apodApi.getApods(
                    BuildConfig.AUTH_CODE,
                    pageRange.startDate,
                    pageRange.endDate
                )
                Log.d("PAGES", "About to insert")
                apodDao.insertAll(apods)
            }
        }

        Log.d("PAGES", "Converting to livedata")
        return apodDao.getAll().toLiveData(
            config = browsePageConfig,
            boundaryCallback = BrowseBoundaryCallback(callback))
    }
}