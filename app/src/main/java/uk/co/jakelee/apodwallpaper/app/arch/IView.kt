package uk.co.jakelee.apodwallpaper.app.arch

interface IView<S: IState> {
    fun render(state: S)
}