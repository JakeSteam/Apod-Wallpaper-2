package uk.co.jakelee.apodwallpaper.extensions

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import uk.co.jakelee.apodwallpaper.R
import uk.co.jakelee.apodwallpaper.app.storage.GlideApp

@BindingAdapter("setImageUrl")
fun ImageView.setImageUrl(imageUrl: String?) = imageUrl?.let {
    GlideApp.with(this.context)
        .load(imageUrl)
        .placeholder(R.drawable.ui_background)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}