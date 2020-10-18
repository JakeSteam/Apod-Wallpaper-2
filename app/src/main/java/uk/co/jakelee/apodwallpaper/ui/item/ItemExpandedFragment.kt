package uk.co.jakelee.apodwallpaper.ui.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import uk.co.jakelee.apodwallpaper.R
import uk.co.jakelee.apodwallpaper.databinding.FragmentExpandedItemBinding

class ItemExpandedFragment : Fragment() {

    private lateinit var binding: FragmentExpandedItemBinding
    private val args: ItemExpandedFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_expanded_item,
            container,
            false
        )
        binding.apodUrl = args.apodUrl
        binding.close.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }
}