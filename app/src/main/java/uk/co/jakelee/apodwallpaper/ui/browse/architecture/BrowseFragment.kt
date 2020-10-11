package uk.co.jakelee.apodwallpaper.ui.browse.architecture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.fragment_browse.*
import kotlinx.android.synthetic.main.fragment_browse.view.*
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import uk.co.jakelee.apodwallpaper.R
import uk.co.jakelee.apodwallpaper.app.architecture.IView
import uk.co.jakelee.apodwallpaper.ui.browse.BrowseAdapter

class BrowseFragment : Fragment(), IView<BrowseState> {

    private val adapter = BrowseAdapter()
    private val browseViewModel: BrowseViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_browse, container, false)
        root.recyclerView.adapter = adapter

        // Observing the state
        browseViewModel.state.observe(viewLifecycleOwner) {
            render(it)
        }

        // Fetching data when the fragment is created
        lifecycleScope.launch {
            browseViewModel.intents.send(BrowseIntent.FetchApods)
        }
        return root
    }

    override fun render(state: BrowseState) {
        with(state) {
            progressBar.isVisible = isLoading
            apods?.let { apodsLiveData ->
                if (!apodsLiveData.hasActiveObservers()) {
                    apodsLiveData.observe(viewLifecycleOwner) {
                        adapter.submitList(it)
                    }
                }
            }

            if (errorMessage != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}