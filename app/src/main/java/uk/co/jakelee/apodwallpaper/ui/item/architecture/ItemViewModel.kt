package uk.co.jakelee.apodwallpaper.ui.item.architecture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import uk.co.jakelee.apodwallpaper.app.ApodDateParser
import uk.co.jakelee.apodwallpaper.app.architecture.IViewModel
import uk.co.jakelee.apodwallpaper.app.database.ApodRepository
import uk.co.jakelee.apodwallpaper.app.storage.FileSystemHelper
import uk.co.jakelee.apodwallpaper.model.Apod
import uk.co.jakelee.apodwallpaper.model.ApodMessage
import java.net.URL

class ItemViewModel(
  private val apodRepository: ApodRepository,
  private val apodDateParser: ApodDateParser,
  private val fileSystemHelper: FileSystemHelper
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
                    is ItemIntent.SaveApod -> saveApod(itemIntent.bitmap)
                    is ItemIntent.FollowingDirection -> clearPendingDirection()
                }
            }
        }
    }

    private fun openApod(apod: Apod) = viewModelScope.launch(Dispatchers.IO) {
        emitApod(apod)
    }

    private fun fetchApod(date: String) = viewModelScope.launch(Dispatchers.IO) {
        updateState { it.copy(isLoading = true, message = null, apod = null) }
        emitApod(apodRepository.getApod(date, true, messageCallback))
    }

    private fun fetchLatest() = viewModelScope.launch(Dispatchers.IO) {
        val todayDate = apodDateParser.currentApodDate()
        updateState { it.copy(isLoading = true, message = null, apod = null) }
        emitApod(apodRepository.getApod(todayDate, false, messageCallback))
    }

    private val messageCallback: (ApodMessage) -> Unit = { message ->
        viewModelScope.launch(Dispatchers.IO) {
            updateState { it.copy(isLoading = false, message = message) }
        }
    }

    private fun expandApod() = viewModelScope.launch(Dispatchers.IO) {
        currentApod?.url?.let { url ->
            updateState { it.copy(pendingDirection = ItemFragmentDirections.expandApod(url)) }
        }
    }

    private fun openPreviousApod() = viewModelScope.launch(Dispatchers.IO) {
        currentApod?.let { apod ->
            apodDateParser.getPreviousDate(apod.date)?.let { previousDate ->
                emitApod(apodRepository.getApod(previousDate, true, messageCallback))
            }
        }
    }

    private fun openNextApod() = viewModelScope.launch(Dispatchers.IO) {
        currentApod?.let { apod ->
            val nextDate = apodDateParser.getNextDate(apod.date)
            if (nextDate != null) {
                emitApod(apodRepository.getApod(nextDate, true, messageCallback))
            } else {
                messageCallback.invoke(ApodMessage("", "You're already viewing the latest APOD!"))
            }
        }
    }

    private fun saveApod(bitmap: Bitmap) = viewModelScope.launch(Dispatchers.IO) {
        currentApod?.let {
            if (!fileSystemHelper.doesImageExist(it.date)) {
                fileSystemHelper.saveImage(bitmap, it.date)
                messageCallback.invoke(ApodMessage("", "\"${it.title}\" saved!"))
            } else {
                messageCallback.invoke(ApodMessage("", "\"${it.title}\" has already been saved!"))
            }
        }
    }

    private suspend fun emitApod(apod: Apod?) {
        currentApod = apod
        updateState { it.copy(isLoading = false, message = null, apod = apod) }
    }

    private fun clearPendingDirection() = viewModelScope.launch(Dispatchers.IO) {
        updateState { it.copy(pendingDirection = null) }
    }

    private suspend fun updateState(handler: suspend (intent: ItemState) -> ItemState) {
        _state.postValue(handler(state.value!!))
    }

}