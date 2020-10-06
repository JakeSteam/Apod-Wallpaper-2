package uk.co.jakelee.apodwallpaper.ui.browse

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
import uk.co.jakelee.apodwallpaper.UserIntent
import uk.co.jakelee.apodwallpaper.R
import uk.co.jakelee.apodwallpaper.UserState
import uk.co.jakelee.apodwallpaper.adapter.ItemAdapter
import uk.co.jakelee.apodwallpaper.app.arch.IView

class BrowseFragment : Fragment(), IView<UserState> {

    private val mAdapter = ItemAdapter()
    private val browseViewModel: BrowseViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_browse, container, false)
        root.recyclerView.adapter = mAdapter

        // Observing the state
        browseViewModel.state.observe(viewLifecycleOwner, Observer {
            render(it)
        })

        // Fetching data when the application launched
        lifecycleScope.launch {
            browseViewModel.intents.send(UserIntent.FetchUsers)
        }
        return root
    }

    override fun render(state: UserState) {
        with(state) {
            progressBar.isVisible = isLoading
            mAdapter.submitList(apods)

            if (errorMessage != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}