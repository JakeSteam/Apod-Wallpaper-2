package uk.co.jakelee.apodwallpaper.ui.browse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BrowseViewModel : ViewModel() {

  private val _text = MutableLiveData<String>().apply {
    value = "This is the browse Fragment"
  }
  val text: LiveData<String> = _text
}