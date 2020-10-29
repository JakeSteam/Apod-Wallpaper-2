package uk.co.jakelee.apodwallpaper.ui.settings

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
            "toNotifications" -> findNavController().navigate(R.id.open_settings_notifications)
        }
    }

}