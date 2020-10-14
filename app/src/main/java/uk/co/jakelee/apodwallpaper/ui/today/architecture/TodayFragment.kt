package uk.co.jakelee.apodwallpaper.ui.today.architecture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_browse.*
import kotlinx.android.synthetic.main.fragment_today.*
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import uk.co.jakelee.apodwallpaper.R
import uk.co.jakelee.apodwallpaper.app.architecture.IView
import uk.co.jakelee.apodwallpaper.model.Apod

class TodayFragment : Fragment(), IView<TodayState> {

    private val todayViewModel: TodayViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_today, container, false)

        todayViewModel.state.observe(viewLifecycleOwner) { render(it) }
        sendIntent(TodayIntent.FetchLatest)

        return root
    }

    override fun render(state: TodayState) {
        with(state) {
            apod?.let { renderApod(it) }
            renderProgress(isLoading)
            errorMessage?.let { renderError(it) }
        }
    }

    private fun renderApod(apod: LiveData<Apod>) {
        apod.observe(viewLifecycleOwner) {
            text_today.text = "${it.date}: ${it.title}"
        }
    }

    private fun renderProgress(isLoading: Boolean) {
        //progress.isVisible = isLoading
    }

    private fun renderError(error: String) {
        //val snackbar = Snackbar.make(coordinatorLayout, error, Snackbar.LENGTH_INDEFINITE)
        //snackbar.setAction(R.string.button_retry) { sendIntent(TodayIntent.FetchLatest) }
        //snackbar.show()
    }

    private fun sendIntent(intent: TodayIntent) {
        lifecycleScope.launch {
            todayViewModel.intents.send(intent)
        }
    }
}