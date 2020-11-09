package uk.co.jakelee.apodwallpaper.app.storage

import android.content.Context
import androidx.preference.PreferenceManager
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule
import uk.co.jakelee.apodwallpaper.R

@GlideModule
class CustomGlideModule: AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val cacheMegabytes = PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(context.getString(R.string.pref_image_cache_size), context.resources.getInteger(R.integer.pref_image_cache_size_default_value))
        val cacheBytes = cacheMegabytes * 1024L * 1024L
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, cacheBytes))
    }

    override fun isManifestParsingEnabled() = false

}