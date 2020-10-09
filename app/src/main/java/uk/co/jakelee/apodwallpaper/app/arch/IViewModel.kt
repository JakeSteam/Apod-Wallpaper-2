package uk.co.jakelee.apodwallpaper.app.arch

import androidx.lifecycle.LiveData
import kotlinx.coroutines.channels.Channel

interface IViewModel<S: IState, I: IIntent> {
    val intents: Channel<I>
    val state: LiveData<S>
}