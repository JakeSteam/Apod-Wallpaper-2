package uk.co.jakelee.apodwallpaper.ui.today

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TodayViewModel : ViewModel() {

  private val _text = MutableLiveData<String>().apply {
    value = "This is the today Fragment"
  }
  val text: LiveData<String> = _text
}