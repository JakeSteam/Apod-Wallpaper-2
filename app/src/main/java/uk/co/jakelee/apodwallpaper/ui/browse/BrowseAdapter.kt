package uk.co.jakelee.apodwallpaper.ui.browse

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import uk.co.jakelee.apodwallpaper.databinding.ItemThumbnailBinding
import uk.co.jakelee.apodwallpaper.model.Apod

class BrowseAdapter : ListAdapter<Apod, BrowseViewHolder>(BrowseAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemThumbnailBinding.inflate(inflater, parent, false)
        return BrowseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrowseViewHolder, position: Int) {
        val binding = holder.binding as ItemThumbnailBinding
        binding.run {
            apod = getItem(position)
            executePendingBindings()
        }
    }

    companion object : DiffUtil.ItemCallback<Apod>() {
        override fun areItemsTheSame(oldItem: Apod, newItem: Apod): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Apod, newItem: Apod): Boolean {
            return oldItem == newItem
        }
    }
}