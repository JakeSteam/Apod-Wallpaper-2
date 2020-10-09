package uk.co.jakelee.apodwallpaper.app.architecture

import androidx.lifecycle.LiveData
import kotlinx.coroutines.channels.Channel
import uk.co.jakelee.apodwallpaper.ui.browse.architecture.BrowseState

interface IViewModel<S: IState, I: IIntent> {
    val intents: Channel<I>
    val state: LiveData<S>
}