package uk.co.jakelee.apodwallpaper.app.architecture

interface IView<S: IState> {
    fun render(state: S)
}