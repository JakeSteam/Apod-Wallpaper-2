package uk.co.jakelee.apodwallpaper.ui.settings

import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
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

    override fun onActionPreferenceClicked(pref: Preference) {
        when (pref.key) {
            getString(R.string.to_reset_app_data) -> {
                (requireActivity().getSystemService(ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
            }
        }
    }
}