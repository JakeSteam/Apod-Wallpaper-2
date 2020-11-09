package uk.co.jakelee.apodwallpaper.ui.settings

import android.os.Bundle
import android.text.format.Formatter
import androidx.lifecycle.lifecycleScope
import androidx.preference.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.cache.DiskCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.jakelee.apodwallpaper.R
import uk.co.jakelee.apodwallpaper.extensions.getFolderInfo
import java.io.File

class SettingsStorageFragment : SettingsBaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateImageCacheSize()
    }

    override val preferencesFile = R.xml.preferences_storage

    override fun onSwitchPreferenceChanged(pref: SwitchPreferenceCompat) {}

    override fun onEditTextPreferenceChanged(pref: EditTextPreference) {}

    override fun onSeekBarPreferenceChanged(pref: SeekBarPreference) {}

    override fun onListPreferenceChanged(pref: ListPreference) {}

    override fun onNavigationPreferenceClicked(pref: Preference) {
        when (pref.key) {
            getString(R.string.to_clear_image_cache) -> clearImageCache()
        }
    }

    private fun clearImageCache() = lifecycleScope.launch(Dispatchers.IO) {
        Glide.get(requireActivity().applicationContext).clearDiskCache()
        updateImageCacheSize()
    }

    private fun updateImageCacheSize() {
        val cacheInfo = File(requireContext().cacheDir, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR).getFolderInfo()
        lifecycleScope.launch(Dispatchers.Main) {
            val clearCache = findPreference<Preference>(getString(R.string.to_clear_image_cache))
            clearCache?.summary = "${Formatter.formatShortFileSize(requireContext(), cacheInfo.bytes)} (${cacheInfo.files} files)"
        }
    }
}