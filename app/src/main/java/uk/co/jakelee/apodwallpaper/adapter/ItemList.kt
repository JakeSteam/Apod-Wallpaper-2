package uk.co.jakelee.apodwallpaper.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import uk.co.jakelee.apodwallpaper.databinding.ItemListBinding
import uk.co.jakelee.apodwallpaper.model.Apod

class ItemAdapter : ListAdapter<Apod, ItemViewHolder>(ItemAdapter) {
    companion object : DiffUtil.ItemCallback<Apod>() {
        override fun areItemsTheSame(oldItem: Apod, newItem: Apod): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Apod, newItem: Apod): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val binding = holder.binding as ItemListBinding
        val currentUser = getItem(position)
        binding.run {
            user = currentUser
            executePendingBindings()
        }
    }
}

class ItemViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)