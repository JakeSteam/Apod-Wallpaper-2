package uk.co.jakelee.apodwallpaper.extensions

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.api.load
import uk.co.jakelee.apodwallpaper.R

@BindingAdapter("setImageUrl")
fun ImageView.setImageUrl(imageUrl: String?) {
    if (imageUrl == null) {
        val placeholder = ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)
        load(placeholder) { crossfade(true) }
    } else {
        load(imageUrl) { crossfade(true) }
    }
}