package uk.co.jakelee.apodwallpaper.ui.browse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uk.co.jakelee.apodwallpaper.UserIntent
import uk.co.jakelee.apodwallpaper.UserState
import uk.co.jakelee.apodwallpaper.app.arch.IModel
import uk.co.jakelee.apodwallpaper.model.ApodApi

class BrowseViewModel(private val apodApi: ApodApi) : ViewModel(), IModel<UserState, UserIntent> {

  override val intents: Channel<UserIntent> = Channel(Channel.UNLIMITED)

  private val _state = MutableLiveData<UserState>().apply { value = UserState() }
  override val state: LiveData<UserState>
    get() = _state

  init {
    handlerIntent()
  }

  private fun handlerIntent() {
    viewModelScope.launch {
      intents.consumeAsFlow().collect { userIntent ->
        when(userIntent) {
          UserIntent.RefreshUsers -> fetchData()
          UserIntent.FetchUsers -> fetchData()
        }
      }
    }
  }

  private fun fetchData() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        updateState { it.copy(isLoading = true) }
        updateState { it.copy(isLoading = false, apods = apodApi.getApods("", "2018-12-02", "2018-12-14")) }
      } catch (e: Exception) {
        updateState { it.copy(isLoading = false, errorMessage = e.message) }
      }
    }
  }

  private suspend fun updateState(handler: suspend (intent: UserState) -> UserState) {
    _state.postValue(handler(state.value!!))
  }
}