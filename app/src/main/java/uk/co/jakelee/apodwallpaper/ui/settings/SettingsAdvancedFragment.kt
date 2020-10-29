package uk.co.jakelee.apodwallpaper.ui.settings

import androidx.preference.*
import uk.co.jakelee.apodwallpaper.R

class SettingsAdvancedFragment : SettingsBaseFragment() {

    override val preferencesFile = R.xml.preferences_advanced

    override fun onSwitchPreferenceChanged(pref: SwitchPreferenceCompat) {}

    override fun onEditTextPreferenceChanged(pref: EditTextPreference) {}

    override fun onSeekBarPreferenceChanged(pref: SeekBarPreference) {}

    override fun onNavigationPreferenceClicked(pref: Preference) {}
}