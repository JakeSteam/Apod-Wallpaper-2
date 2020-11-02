package uk.co.jakelee.apodwallpaper.ui.settings

import androidx.appcompat.app.AppCompatDelegate
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
        val theme = when (pref) {
            "AUTO" -> MODE_NIGHT_FOLLOW_SYSTEM
            "DARK" -> MODE_NIGHT_YES
            "LIGHT" -> MODE_NIGHT_NO
            else -> null
        }
        theme?.let { setDefaultNightMode(it) }
    }

    override fun onNavigationPreferenceClicked(pref: Preference) {}
}