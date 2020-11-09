package uk.co.jakelee.apodwallpaper.ui.settings

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.preference.*
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.jakelee.apodwallpaper.R

class SettingsStorageFragment : SettingsBaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateImageCacheSize("initial")
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
        updateImageCacheSize("done")
    }

    private fun updateImageCacheSize(size: String) {
        lifecycleScope.launch {
            val clearCache = findPreference<Preference>(getString(R.string.to_clear_image_cache))
            clearCache?.summary = size
        }
    }
}