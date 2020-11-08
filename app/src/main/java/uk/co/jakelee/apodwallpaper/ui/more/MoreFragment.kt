package uk.co.jakelee.apodwallpaper.ui.more

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.preference.*
import uk.co.jakelee.apodwallpaper.BuildConfig
import uk.co.jakelee.apodwallpaper.R
import uk.co.jakelee.apodwallpaper.ui.settings.SettingsBaseFragment

class MoreFragment : SettingsBaseFragment() {

    override val preferencesFile = R.xml.more

    override fun onSwitchPreferenceChanged(pref: SwitchPreferenceCompat) {}

    override fun onEditTextPreferenceChanged(pref: EditTextPreference) {}

    override fun onSeekBarPreferenceChanged(pref: SeekBarPreference) {}

    override fun onListPreferenceChanged(pref: ListPreference) {}

    override fun onNavigationPreferenceClicked(pref: Preference) {
        when (pref.key) {
            getString(R.string.to_give_feedback) -> showGiveFeedbackDialog()
            getString(R.string.to_source_code) -> openUrl(getString(R.string.pref_source_code_url))
            getString(R.string.to_planning_board) -> openUrl(getString(R.string.pref_planning_board_url))
            getString(R.string.to_other_apps) -> openUrl(getString(R.string.pref_other_apps_url))
        }
    }

    private fun showGiveFeedbackDialog() = AlertDialog.Builder(requireActivity())
        .setTitle(R.string.pref_give_feedback_title)
        .setMessage(R.string.pref_give_feedback_desc)
        .setPositiveButton(R.string.pref_give_feedback_store) { _, _ ->
            openUrl(getString(R.string.pref_give_feedback_store_url, BuildConfig.APPLICATION_ID))
        }
        .setNegativeButton(R.string.pref_give_feedback_email) { _, _ ->
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse(getString(R.string.pref_give_feedback_email_mailto))
            startActivity(intent)
        }
        .setNeutralButton(R.string.pref_give_feedback_twitter) { _, _ ->
            openUrl(getString(R.string.pref_give_feedback_twitter_url))
        }
        .show()

    private fun openUrl(url: String) {
        try {
            val uri = Uri.parse(url)
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: Exception) { }
    }
}