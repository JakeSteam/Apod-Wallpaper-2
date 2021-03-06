package uk.co.jakelee.apodwallpaper.ui.browse.architecture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import uk.co.jakelee.apodwallpaper.NavigationDirections
import uk.co.jakelee.apodwallpaper.app.architecture.IViewModel
import uk.co.jakelee.apodwallpaper.app.database.ApodRepository
import uk.co.jakelee.apodwallpaper.model.Apod

class BrowseViewModel(
  private val apodRepository: ApodRepository
) : ViewModel(), IViewModel<BrowseState, BrowseIntent> {

    override val intents: Channel<BrowseIntent> = Channel(Channel.UNLIMITED)

    private val _state = MutableLiveData<BrowseState>().apply {
        value = BrowseState()
    }

    override val state: LiveData<BrowseState>
        get() = _state

    init {
        subscribeToIntents()
    }

    private fun subscribeToIntents() {
        viewModelScope.launch {
            intents.consumeAsFlow().collect { browseIntent ->
                when (browseIntent) {
                    is BrowseIntent.FetchApods -> fetchData()
                    is BrowseIntent.OpenApod -> openApod(browseIntent.apod)
                    is BrowseIntent.FollowingDirection -> clearPendingDirection()
                }
            }
        }
    }

    private fun fetchData() = viewModelScope.launch(Dispatchers.IO) {
        updateState { it.copy(isLoading = true, pendingDirection = null) }
        updateState { it.copy(isLoading = false, apods = apodRepository.getApods(viewModelScope, errorCallback)) }
    }

    private val errorCallback: (String) -> Unit = { error ->
        viewModelScope.launch(Dispatchers.IO) {
            updateState { it.copy(isLoading = false, errorMessage = error) }
        }
    }

    private fun openApod(apod: Apod) = viewModelScope.launch(Dispatchers.IO) {
        updateState { it.copy(isLoading = false, pendingDirection = NavigationDirections.openApod(apod, null)) }
    }

    private fun clearPendingDirection() = viewModelScope.launch(Dispatchers.IO) {
        updateState { it.copy(pendingDirection = null) }
    }

    private suspend fun updateState(handler: suspend (intent: BrowseState) -> BrowseState) {
        _state.postValue(handler(state.value!!))
    }
}