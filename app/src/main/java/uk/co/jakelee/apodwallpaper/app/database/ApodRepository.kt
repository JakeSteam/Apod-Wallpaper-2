package uk.co.jakelee.apodwallpaper.app.database

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.jakelee.apodwallpaper.BuildConfig
import uk.co.jakelee.apodwallpaper.app.ApodDateParser
import uk.co.jakelee.apodwallpaper.model.Apod
import uk.co.jakelee.apodwallpaper.model.ApodMessage
import uk.co.jakelee.apodwallpaper.network.ApodApi
import uk.co.jakelee.apodwallpaper.ui.browse.BrowseBoundaryCallback

class ApodRepository(
    private val apodDao: ApodDao,
    private val apodApi: ApodApi,
    private val apodDateParser: ApodDateParser
) {

    private var currentPage = 0
    private val browsePageConfig = PagedList.Config.Builder()
        .setPageSize(9)
        .setPrefetchDistance(36)
        .setEnablePlaceholders(true)
        .build()

    suspend fun getApod(date: String, explicit: Boolean, messageCallback: ((ApodMessage) -> Unit)?): Apod? {
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
            messageCallback?.invoke(ApodMessage(date, e.message ?: ""))
            return null
        }
    }

    suspend fun getApods(callbackScope: CoroutineScope, messageCallback: ((String) -> Unit)?): LiveData<PagedList<Apod>> {
        val callback: () -> Unit = {
            callbackScope.launch(Dispatchers.IO) {
                try {
                    val pageRange = apodDateParser.pageToDateRange(currentPage)
                    val apods = apodApi.getApods(
                        BuildConfig.AUTH_CODE,
                        pageRange.startDate,
                        pageRange.endDate
                    )
                    apodDao.insertAll(apods)
                    currentPage++
                } catch (e: Exception) {
                    messageCallback?.invoke(e.message ?: "")
                }
            }
        }
        return apodDao.getAll().toLiveData(
            config = browsePageConfig,
            boundaryCallback = BrowseBoundaryCallback(callback))
    }
}