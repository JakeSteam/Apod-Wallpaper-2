package uk.co.jakelee.apodwallpaper.extensions

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import uk.co.jakelee.apodwallpaper.R

@BindingAdapter("setImageUrl")
fun ImageView.setImageUrl(imageUrl: String?) = imageUrl?.let {
    Glide.with(this.context)
        .load(imageUrl)
        .placeholder(R.color.ui_background)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}