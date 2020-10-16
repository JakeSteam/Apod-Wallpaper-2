package uk.co.jakelee.apodwallpaper.app.database

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.jakelee.apodwallpaper.BuildConfig
import uk.co.jakelee.apodwallpaper.model.Apod
import uk.co.jakelee.apodwallpaper.network.ApodApi
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

    suspend fun getApod(date: String, explicit: Boolean, errorCallback: ((String) -> Unit)?): Apod? {
        try {
            val localApod = apodDao.getByDate(date)
            if (localApod != null) { return localApod }

            val remoteApod = if (explicit) {
                apodApi.getApod(BuildConfig.AUTH_CODE, date)
            } else {
                apodApi.getLatestApod(BuildConfig.AUTH_CODE)
            }
            apodDao.insert(remoteApod)
            return remoteApod
        } catch (e: Exception) {
            errorCallback?.invoke(e.message ?: "")
            return null
        }
    }

    suspend fun getApods(callbackScope: CoroutineScope, errorCallback: ((String) -> Unit)?): LiveData<PagedList<Apod>> {
        val callback: () -> Unit = {
            callbackScope.launch(Dispatchers.IO) {
                try {
                    val pageRange = pageToDateRange(currentPage)
                    val apods = apodApi.getApods(
                        BuildConfig.AUTH_CODE,
                        pageRange.startDate,
                        pageRange.endDate
                    )
                    apodDao.insertAll(apods)
                    currentPage++
                } catch (e: Exception) {
                    errorCallback?.invoke(e.message ?: "")
                }
            }
        }
        return apodDao.getAll().toLiveData(
            config = browsePageConfig,
            boundaryCallback = BrowseBoundaryCallback(callback))
    }
}