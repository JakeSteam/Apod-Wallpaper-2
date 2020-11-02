package uk.co.jakelee.apodwallpaper.ui.browse.architecture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_browse.*
import kotlinx.android.synthetic.main.fragment_browse.coordinatorLayout
import kotlinx.android.synthetic.main.fragment_browse.view.*
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import uk.co.jakelee.apodwallpaper.R
import uk.co.jakelee.apodwallpaper.app.architecture.IView
import uk.co.jakelee.apodwallpaper.model.Apod
import uk.co.jakelee.apodwallpaper.ui.browse.BrowseAdapter

class BrowseFragment : Fragment(), IView<BrowseState> {

    private val adapter = BrowseAdapter { apod: Apod -> sendIntent(BrowseIntent.OpenApod(apod)) }
    private val browseViewModel: BrowseViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_browse, container, false)
        root.recyclerView.adapter = adapter

        browseViewModel.state.observe(viewLifecycleOwner) { render(it) }
        sendIntent(BrowseIntent.FetchApods)

        return root
    }

    override fun render(state: BrowseState) {
        with(state) {
            pendingDirection?.let {
                findNavController().navigate(pendingDirection)
                sendIntent(BrowseIntent.FollowingDirection)
            }
            apods?.let { renderApods(it) }
            renderProgress(isLoading)
            errorMessage?.let { renderError(it) }
        }
    }

    private fun renderApods(apods: LiveData<PagedList<Apod>>) {
        apods.removeObservers(viewLifecycleOwner)
        apods.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    private fun renderProgress(isLoading: Boolean) {
        progress.isVisible = isLoading
    }

    private fun renderError(error: String) {
        Snackbar.make(coordinatorLayout, error, Snackbar.LENGTH_LONG).apply {
            setAction(R.string.button_retry) { sendIntent(BrowseIntent.FetchApods) }
            show()
        }
    }

    private fun sendIntent(intent: BrowseIntent) {
        lifecycleScope.launch {
            browseViewModel.intents.send(intent)
        }
    }
}