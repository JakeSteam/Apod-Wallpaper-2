package uk.co.jakelee.apodwallpaper.ui.settings

import android.os.Bundle
import android.text.format.Formatter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.preference.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.cache.DiskCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import uk.co.jakelee.apodwallpaper.R
import uk.co.jakelee.apodwallpaper.app.database.ApodDao
import uk.co.jakelee.apodwallpaper.app.storage.FileSystemHelper
import uk.co.jakelee.apodwallpaper.extensions.getFolderInfo
import java.io.File

class SettingsStorageFragment : SettingsBaseFragment() {

    private val apodDao: ApodDao by inject { parametersOf(this) }
    private val fileSystemHelper: FileSystemHelper by inject { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            updateImageCacheSize()
            updateSavedImagesSize()
            updateDatabaseSize()
        }
    }

    override val preferencesFile = R.xml.preferences_storage

    override fun onSwitchPreferenceChanged(pref: SwitchPreferenceCompat) {
        when (pref.key) {
            getString(R.string.pref_use_jpegs) -> {
                showClearImagesSuggestDialog()
            }
        }
    }

    override fun onEditTextPreferenceChanged(pref: EditTextPreference) {}

    override fun onSeekBarPreferenceChanged(pref: SeekBarPreference) {
        when (pref.key) {
            getString(R.string.pref_image_cache_size) -> { /* TODO: Reload config */
            }
        }
    }

    override fun onListPreferenceChanged(pref: ListPreference) {}

    override fun onActionPreferenceClicked(pref: Preference) {
        when (pref.key) {
            getString(R.string.clear_image_cache) -> clearImageCache()
            getString(R.string.clear_saved_images) -> showClearImagesConfirmDialog()
            getString(R.string.clear_database) -> clearDatabase()
        }
    }

    private fun clearImageCache() = lifecycleScope.launch(Dispatchers.IO) {
        Glide.get(requireActivity().applicationContext).clearDiskCache()
        updateImageCacheSize()
    }

    private fun updateImageCacheSize() {
        val cacheFolder = File(
            requireContext().cacheDir,
            DiskCache.Factory.DEFAULT_DISK_CACHE_DIR
        ).getFolderInfo()
        lifecycleScope.launch(Dispatchers.Main) {
            val clearCache = findPreference<Preference>(getString(R.string.clear_image_cache))
            clearCache?.summary = getString(
                R.string.storage_display,
                Formatter.formatShortFileSize(requireContext(), cacheFolder.bytes),
                cacheFolder.files
            )
        }
    }

    private fun showClearImagesSuggestDialog() = AlertDialog.Builder(requireActivity())
        .setTitle(R.string.clear_images_dialog_title)
        .setMessage(R.string.clear_images_dialog_message_prompt)
        .setPositiveButton(R.string.yes) { _, _ -> clearSavedImages() }
        .setNeutralButton(R.string.cancel) { _, _ -> }
        .show()

    private fun showClearImagesConfirmDialog() = AlertDialog.Builder(requireActivity())
        .setTitle(R.string.clear_images_dialog_title)
        .setMessage(R.string.clear_images_dialog_message_confirm)
        .setPositiveButton(R.string.yes) { _, _ -> clearSavedImages() }
        .setNeutralButton(R.string.cancel) { _, _ -> }
        .show()

    private fun clearSavedImages() = lifecycleScope.launch(Dispatchers.IO) {
        fileSystemHelper.deleteAllImages()
        updateSavedImagesSize()
    }

    private fun updateSavedImagesSize() {
        val savedFolder = fileSystemHelper.getSavedImagesInfo()
        lifecycleScope.launch(Dispatchers.Main) {
            val clearSaved = findPreference<Preference>(getString(R.string.clear_saved_images))
            clearSaved?.summary = getString(
                R.string.storage_display,
                Formatter.formatShortFileSize(requireContext(), savedFolder.bytes),
                savedFolder.files
            )
        }
    }

    private fun clearDatabase() = lifecycleScope.launch(Dispatchers.IO) {
        apodDao.deleteAll()
        updateDatabaseSize()
    }

    private fun updateDatabaseSize() {
        val numRows = apodDao.getCount()
        lifecycleScope.launch(Dispatchers.Main) {
            val clearDatabase = findPreference<Preference>(getString(R.string.clear_database))
            clearDatabase?.summary = getString(R.string.storage_database_display, numRows)
        }
    }
}