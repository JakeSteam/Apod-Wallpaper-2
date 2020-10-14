package uk.co.jakelee.apodwallpaper.ui.today.architecture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import uk.co.jakelee.apodwallpaper.app.architecture.IViewModel
import uk.co.jakelee.apodwallpaper.app.database.ApodRepository

class TodayViewModel(
  private val apodRepository: ApodRepository
) : ViewModel(), IViewModel<TodayState, TodayIntent> {

    override val intents: Channel<TodayIntent> = Channel(Channel.UNLIMITED)

    private val _state = MutableLiveData<TodayState>().apply {
        value = TodayState()
    }

    override val state: LiveData<TodayState>
        get() = _state

    init {
        subscribeToIntents()
    }

    private fun subscribeToIntents() {
        viewModelScope.launch {
            intents.consumeAsFlow().collect { browseIntent ->
                when (browseIntent) {
                  TodayIntent.FetchLatest -> fetchLatest()
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
            updateState { it.copy(isLoading = true) }
            updateState { it.copy(isLoading = false, apod = apodRepository.getLatestApod(errorCallback)) }
        }
    }

    private suspend fun updateState(handler: suspend (intent: TodayState) -> TodayState) {
        _state.postValue(handler(state.value!!))
    }
}