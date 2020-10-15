package uk.co.jakelee.apodwallpaper.ui.item.architecture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_item.*
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import uk.co.jakelee.apodwallpaper.R
import uk.co.jakelee.apodwallpaper.app.architecture.IView
import uk.co.jakelee.apodwallpaper.model.Apod
import uk.co.jakelee.apodwallpaper.ui.browse.architecture.BrowseFragmentDirections
import uk.co.jakelee.apodwallpaper.ui.browse.architecture.BrowseIntent

class ItemFragment : Fragment(), IView<ItemState> {

    private val itemViewModel: ItemViewModel by viewModel()
    private val args: ItemFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_item, container, false)
        itemViewModel.state.observe(viewLifecycleOwner) { render(it) }
        when {
            args.apod != null -> sendIntent(ItemIntent.OpenApod(args.apod))
            args.date != null -> sendIntent(ItemIntent.OpenDate(args.date.toString()))
            else -> sendIntent(ItemIntent.FetchLatest)
        }

        return root
    }

    override fun render(state: ItemState) {
        with(state) {
            apod?.let { renderApod(it) }
            renderProgress(isLoading)
            errorMessage?.let { renderError(it) }
        }
    }

    private fun renderApod(apod: Apod) {
        title.text = "${apod.date}: ${apod.title}"
    }

    private fun renderProgress(isLoading: Boolean) {
        progressBar.isVisible = isLoading
    }

    private fun renderError(error: String) {
        val snackbar = Snackbar.make(coordinatorLayout, error, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(R.string.button_retry) { sendIntent(ItemIntent.FetchLatest) }
        snackbar.show()
    }

    private fun sendIntent(intent: ItemIntent) {
        lifecycleScope.launch {
            itemViewModel.intents.send(intent)
        }
    }
}