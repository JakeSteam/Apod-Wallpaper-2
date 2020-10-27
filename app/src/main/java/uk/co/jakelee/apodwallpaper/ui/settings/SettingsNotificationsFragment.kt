package uk.co.jakelee.apodwallpaper.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.*
import uk.co.jakelee.apodwallpaper.R

class SettingsNotificationsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setHasOptionsMenu(true)
        addPreferencesFromResource(R.xml.preferences_notifications)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val pref: Preference = findPreference(key) ?: return
        when (pref) {
            is SwitchPreferenceCompat -> { }
            is EditTextPreference -> { }
            is SeekBarPreference -> { }
        }
    }
}