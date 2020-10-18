package uk.co.jakelee.apodwallpaper.ui.item.architecture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_item.*
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import uk.co.jakelee.apodwallpaper.R
import uk.co.jakelee.apodwallpaper.app.architecture.IView
import uk.co.jakelee.apodwallpaper.databinding.FragmentItemBinding
import uk.co.jakelee.apodwallpaper.ui.browse.architecture.BrowseIntent

class ItemExpandedFragment : Fragment(), IView<ItemState> {

    private lateinit var binding: FragmentItemBinding
    private val itemViewModel: ItemViewModel by viewModel()
    private val args: ItemFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_item, container, false)
        itemViewModel.state.observe(viewLifecycleOwner) { render(it) }
        when {
            args.apod != null -> sendIntent(ItemIntent.OpenApod(args.apod!!))
            args.date != null -> sendIntent(ItemIntent.OpenDate(args.date.toString()))
            else -> sendIntent(ItemIntent.FetchLatest)
        }

        return binding.root
    }

    override fun render(state: ItemState) {
        with(state) {
            pendingDirection?.let {
                findNavController().navigate(pendingDirection)
                sendIntent(ItemIntent.FollowingDirection)
            }
            apod?.let { binding.apod = apod }
            binding.isLoading = isLoading
            errorMessage?.let { renderError(it) }
        }
    }

    private fun renderError(error: String) {
        Snackbar.make(coordinatorLayout, error, Snackbar.LENGTH_INDEFINITE).apply {
            setAction(R.string.button_retry) { sendIntent(ItemIntent.FetchLatest) }
            show()
        }
    }

    private fun sendIntent(intent: ItemIntent) {
        lifecycleScope.launch {
            itemViewModel.intents.send(intent)
        }
    }
}