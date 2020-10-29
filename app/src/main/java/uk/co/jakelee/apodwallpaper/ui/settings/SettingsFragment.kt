package uk.co.jakelee.apodwallpaper.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.navigation.fragment.findNavController
import androidx.preference.*
import uk.co.jakelee.apodwallpaper.R

class SettingsFragment : SettingsBaseFragment() {

    override val preferencesFile = R.xml.preferences

    override fun onSwitchPreferenceChanged(pref: SwitchPreferenceCompat) {}

    override fun onEditTextPreferenceChanged(pref: EditTextPreference) {}

    override fun onSeekBarPreferenceChanged(pref: SeekBarPreference) {}

    override fun onNavigationPreferenceClicked(pref: Preference) {
        when (pref.key) {
            getString(R.string.to_advanced_settings) -> findNavController().navigate(R.id.open_settings_advanced)
            getString(R.string.to_image_settings) -> findNavController().navigate(R.id.open_settings_images)
            getString(R.string.to_notification_settings) -> findNavController().navigate(R.id.open_settings_notifications)
            getString(R.string.to_scheduling_settings) -> findNavController().navigate(R.id.open_settings_scheduling)
            getString(R.string.to_storage_settings) -> findNavController().navigate(R.id.open_settings_storage)
            getString(R.string.to_generate_key) -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://api.nasa.gov/")))
        }
    }

}