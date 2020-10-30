package uk.co.jakelee.apodwallpaper.ui.settings

import androidx.preference.*
import uk.co.jakelee.apodwallpaper.R

class SettingsSchedulingFragment : SettingsBaseFragment() {

    override val preferencesFile = R.xml.preferences_scheduling

    override fun onSwitchPreferenceChanged(pref: SwitchPreferenceCompat) {}

    override fun onEditTextPreferenceChanged(pref: EditTextPreference) {}

    override fun onSeekBarPreferenceChanged(pref: SeekBarPreference) {}

    override fun onListPreferenceChanged(pref: ListPreference) {}

    override fun onNavigationPreferenceClicked(pref: Preference) {}
}