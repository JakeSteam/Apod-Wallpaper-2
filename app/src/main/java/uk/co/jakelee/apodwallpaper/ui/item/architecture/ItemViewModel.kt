package uk.co.jakelee.apodwallpaper.ui.item.architecture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import uk.co.jakelee.apodwallpaper.app.architecture.IViewModel
import uk.co.jakelee.apodwallpaper.app.database.ApodRepository

class ItemViewModel(
  private val apodRepository: ApodRepository
) : ViewModel(), IViewModel<ItemState, ItemIntent> {

    override val intents: Channel<ItemIntent> = Channel(Channel.UNLIMITED)

    private val _state = MutableLiveData<ItemState>().apply {
        value = ItemState()
    }

    override val state: LiveData<ItemState>
        get() = _state

    init {
        subscribeToIntents()
    }

    private fun subscribeToIntents() {
        viewModelScope.launch {
            intents.consumeAsFlow().collect { browseIntent ->
                when (browseIntent) {
                  ItemIntent.FetchLatest -> fetchLatest()
                }
            }
        }
    }

    private val errorCallback: (String) -> Unit = { error ->
        viewModelScope.launch(Dispatchers.IO) {
            updateState { it.copy(isLoading = false, errorMessage = error) }
        }
    }

    private fun fetchLatest() {
        viewModelScope.launch(Dispatchers.IO) {
            updateState { it.copy(isLoading = true, errorMessage = null, apod = null) }
            updateState { it.copy(isLoading = false, errorMessage = null, apod = apodRepository.getLatestApod(errorCallback)) }
        }
    }

    private suspend fun updateState(handler: suspend (intent: ItemState) -> ItemState) {
        _state.postValue(handler(state.value!!))
    }
}