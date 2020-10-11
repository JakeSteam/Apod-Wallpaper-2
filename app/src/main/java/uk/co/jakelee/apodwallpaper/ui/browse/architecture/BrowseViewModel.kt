package uk.co.jakelee.apodwallpaper.ui.browse.architecture

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

  private fun fetchData() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        updateState { it.copy(isLoading = true) }
        updateState {
          val apodLiveData = apodDao.getAllPaged().toLiveData(
            pageSize = 6,
            boundaryCallback = BrowseBoundaryCallback(apodDao, apodApi, viewModelScope)
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
}