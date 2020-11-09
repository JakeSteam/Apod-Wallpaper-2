package uk.co.jakelee.apodwallpaper.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.annotation.XmlRes
import androidx.preference.*

abstract class SettingsBaseFragment: PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    @get:XmlRes
    abstract val preferencesFile: Int

    abstract fun onSwitchPreferenceChanged(pref: SwitchPreferenceCompat)
    abstract fun onEditTextPreferenceChanged(pref: EditTextPreference)
    abstract fun onSeekBarPreferenceChanged(pref: SeekBarPreference)
    abstract fun onListPreferenceChanged(pref: ListPreference)

    abstract fun onActionPreferenceClicked(pref: Preference)

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference?.isPersistent == false) {
            onActionPreferenceClicked(preference)
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(preferencesFile)
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
            is SwitchPreferenceCompat -> onSwitchPreferenceChanged(pref)
            is EditTextPreference -> onEditTextPreferenceChanged(pref)
            is SeekBarPreference -> onSeekBarPreferenceChanged(pref)
            is ListPreference -> onListPreferenceChanged(pref)
        }
    }

}