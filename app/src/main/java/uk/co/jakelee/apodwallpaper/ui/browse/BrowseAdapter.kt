package uk.co.jakelee.apodwallpaper.ui.browse

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import uk.co.jakelee.apodwallpaper.databinding.ItemThumbnailBinding
import uk.co.jakelee.apodwallpaper.model.Apod

class BrowseAdapter(private val clickListener: ((Apod) -> Unit)) : PagedListAdapter<Apod, BrowseViewHolder>(BrowseAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemThumbnailBinding.inflate(inflater, parent, false)
        return BrowseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrowseViewHolder, position: Int) {
        val binding = holder.binding as ItemThumbnailBinding
        binding.run {
            apod = getItem(position)
            holder.itemView.setOnClickListener { clickListener.invoke(apod) }
            executePendingBindings()
        }
    }

    companion object : DiffUtil.ItemCallback<Apod>() {
        override fun areItemsTheSame(oldItem: Apod, newItem: Apod) = oldItem.date == newItem.date
        override fun areContentsTheSame(oldItem: Apod, newItem: Apod) = oldItem == newItem
    }
}