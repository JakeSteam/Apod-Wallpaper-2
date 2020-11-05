package uk.co.jakelee.apodwallpaper.ui.settings

import androidx.appcompat.app.AppCompatDelegate.*
import androidx.preference.*
import uk.co.jakelee.apodwallpaper.R

class SettingsAdvancedFragment : SettingsBaseFragment() {

    override val preferencesFile = R.xml.preferences_advanced

    override fun onSwitchPreferenceChanged(pref: SwitchPreferenceCompat) {}

    override fun onEditTextPreferenceChanged(pref: EditTextPreference) {}

    override fun onSeekBarPreferenceChanged(pref: SeekBarPreference) {}

    override fun onListPreferenceChanged(pref: ListPreference) {
        when (pref.key) {
            getString(R.string.pref_theme) -> handleThemeChange(pref.value)
        }
    }

    private fun handleThemeChange(pref: String) {
        pref.toIntOrNull()?.let {
            setDefaultNightMode(it)
        }
    }

    override fun onNavigationPreferenceClicked(pref: Preference) {}
}