package uk.co.jakelee.apodwallpaper.ui.browse.architecture

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.toLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uk.co.jakelee.apodwallpaper.BuildConfig
import uk.co.jakelee.apodwallpaper.app.architecture.IViewModel
import uk.co.jakelee.apodwallpaper.app.database.ApodDao
import uk.co.jakelee.apodwallpaper.model.Apod
import uk.co.jakelee.apodwallpaper.model.ApodApi
import uk.co.jakelee.apodwallpaper.ui.browse.BrowseBoundaryCallback
import java.util.*

class BrowseViewModel(
  private val apodApi: ApodApi,
  private val apodDao: ApodDao
) : ViewModel(), IViewModel<BrowseState, BrowseIntent> {

  override val intents: Channel<BrowseIntent> = Channel(Channel.UNLIMITED)

  private val _state = MutableLiveData<BrowseState>().apply { value =
    BrowseState()
  }
  override val state: LiveData<BrowseState>
    get() = _state

  init {
    subscribeToIntents()
  }

  private fun subscribeToIntents() {
    viewModelScope.launch {
      intents.consumeAsFlow().collect { browseIntent ->
        when(browseIntent) {
          BrowseIntent.RefreshApods -> fetchData()
          BrowseIntent.FetchApods -> fetchData()
        }
      }
    }
  }

  private var page = 0
  private val fetchData: () -> Unit = {
    viewModelScope.launch(Dispatchers.IO) {
      Log.d("PAGES", "Page: $page")
      val pageRange = pageToDateRange(page++)
      Log.d("PAGES", "Start: ${pageRange.startDate}, end: ${pageRange.endDate}")
      val apods = apodApi.getApods(BuildConfig.AUTH_CODE, pageRange.startDate, pageRange.endDate)
      apodDao.insertAll(apods)
    }
  }

  private val browsePageConfig = PagedList.Config.Builder()
    .setPageSize(9)
    .setPrefetchDistance(36)
    .build()

  private fun fetchData() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        updateState { it.copy(isLoading = true) }
        updateState {
          val apodLiveData = apodDao.getAllPaged().toLiveData(
            config = browsePageConfig,
            boundaryCallback = BrowseBoundaryCallback(fetchData)
          )
          it.copy(isLoading = false, apods = apodLiveData)
        }
      } catch (e: Exception) {
        updateState { it.copy(isLoading = false, errorMessage = e.message) }
      }
    }
  }

  private suspend fun updateState(handler: suspend (intent: BrowseState) -> BrowseState) {
    _state.postValue(handler(state.value!!))
  }

  data class ApodDateRange(val startDate: String, val endDate: String)

  // TODO: Use simpledateformat to display date
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