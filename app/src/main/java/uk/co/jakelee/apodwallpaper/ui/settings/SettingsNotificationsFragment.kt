package uk.co.jakelee.apodwallpaper.ui.settings

import androidx.preference.*
import uk.co.jakelee.apodwallpaper.R

class SettingsNotificationsFragment : SettingsBaseFragment() {

    override val preferencesFile = R.xml.preferences_notifications

    override fun onSwitchPreferenceChanged(pref: SwitchPreferenceCompat) {}

    override fun onEditTextPreferenceChanged(pref: EditTextPreference) {}

    override fun onSeekBarPreferenceChanged(pref: SeekBarPreference) {}
}