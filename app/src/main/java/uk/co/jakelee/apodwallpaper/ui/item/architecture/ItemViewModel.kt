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
import uk.co.jakelee.apodwallpaper.NavigationDirections
import uk.co.jakelee.apodwallpaper.app.architecture.IViewModel
import uk.co.jakelee.apodwallpaper.app.database.ApodRepository
import uk.co.jakelee.apodwallpaper.model.Apod
import java.util.*

class ItemViewModel(
  private val apodRepository: ApodRepository
) : ViewModel(), IViewModel<ItemState, ItemIntent> {

    override val intents: Channel<ItemIntent> = Channel(Channel.UNLIMITED)

    private val _state = MutableLiveData<ItemState>().apply {
        value = ItemState()
    }

    override val state: LiveData<ItemState>
        get() = _state

    private var currentApod: Apod? = null

    init {
        subscribeToIntents()
    }

    private fun subscribeToIntents() {
        viewModelScope.launch {
            intents.consumeAsFlow().collect { itemIntent ->
                when (itemIntent) {
                    is ItemIntent.OpenApod -> openApod(itemIntent.apod)
                    is ItemIntent.OpenDate -> fetchApod(itemIntent.date)
                    is ItemIntent.FetchLatest -> fetchLatest()
                    is ItemIntent.ExpandApod -> expandApod()
                    is ItemIntent.PreviousApod -> openPreviousApod()
                    is ItemIntent.NextApod -> openNextApod()
                    is ItemIntent.FollowingDirection -> clearPendingDirection()
                }
            }
        }
    }

    private fun openApod(apod: Apod) = viewModelScope.launch(Dispatchers.IO) {
        currentApod = apod
        updateState { it.copy(isLoading = false, errorMessage = null, apod = apod) }
    }

    private fun fetchApod(date: String) = viewModelScope.launch(Dispatchers.IO) {
        updateState { it.copy(isLoading = true, errorMessage = null, apod = null) }
        val apod = apodRepository.getApod(date, true, errorCallback)
        currentApod = apod
        updateState { it.copy(isLoading = false, errorMessage = null, apod = apod) }
    }

    private fun fetchLatest() = viewModelScope.launch(Dispatchers.IO) {
        val todayDate = getTodaysDate()
        updateState { it.copy(isLoading = true, errorMessage = null, apod = null) }
        val apod = apodRepository.getApod(todayDate, false, errorCallback)
        currentApod = apod
        updateState { it.copy(isLoading = false, errorMessage = null, apod = apod) }
    }

    private val errorCallback: (String) -> Unit = { error ->
        viewModelScope.launch(Dispatchers.IO) {
            updateState { it.copy(isLoading = false, errorMessage = error) }
        }
    }

    private fun expandApod() = viewModelScope.launch(Dispatchers.IO) {
        currentApod?.url?.let { url ->
            updateState { it.copy(pendingDirection = ItemFragmentDirections.expandApod(url)) }
        }
    }

    private fun openPreviousApod() {

    }

    private fun openNextApod() {

    }

    private fun clearPendingDirection() = viewModelScope.launch(Dispatchers.IO) {
        updateState { it.copy(pendingDirection = null) }
    }

    private suspend fun updateState(handler: suspend (intent: ItemState) -> ItemState) {
        _state.postValue(handler(state.value!!))
    }

    // TODO: Extract to a general "get APOD style date" class
    private fun getTodaysDate(): String {
        val cal = Calendar.getInstance()
        return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}"
    }
}