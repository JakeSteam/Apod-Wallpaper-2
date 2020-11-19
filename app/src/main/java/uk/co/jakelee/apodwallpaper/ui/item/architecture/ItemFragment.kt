package uk.co.jakelee.apodwallpaper.ui.item.architecture

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.WorkManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.datepicker.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_item.*
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import uk.co.jakelee.apodwallpaper.ActionBarActivity
import uk.co.jakelee.apodwallpaper.R
import uk.co.jakelee.apodwallpaper.app.ApodDateParser
import uk.co.jakelee.apodwallpaper.app.architecture.IView
import uk.co.jakelee.apodwallpaper.app.storage.GlideApp
import uk.co.jakelee.apodwallpaper.app.work.ApodWorker
import uk.co.jakelee.apodwallpaper.databinding.FragmentItemBinding
import uk.co.jakelee.apodwallpaper.model.ApodMessage
import java.util.*

class ItemFragment : Fragment(), IView<ItemState> {

    private lateinit var binding: FragmentItemBinding
    private val itemViewModel: ItemViewModel by viewModel()
    private val args: ItemFragmentArgs by navArgs()

    private val parser = ApodDateParser()
    private val calendarConstraint = CalendarConstraints.Builder()
        .setValidator(CompositeDateValidator.allOf(listOf(
            DateValidatorPointForward.from(Calendar.getInstance().apply { set(1995, 5, 15) }.timeInMillis),
            DateValidatorPointBackward.now()
        ))).build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_item, container, false)
        binding.previous.setOnClickListener { sendIntent(ItemIntent.PreviousApod) }
        binding.calendar.setOnClickListener { showDatePicker() }
        binding.expand.setOnClickListener { sendIntent(ItemIntent.ExpandApod) }
        binding.next.setOnClickListener { sendIntent(ItemIntent.NextApod) }
        binding.save.setOnClickListener { showSaveDialog() }
        binding.share.setOnClickListener {
            WorkManager
                .getInstance(requireActivity())
                .enqueue(ApodWorker.getOneOffWorkRequest())
        }

        itemViewModel.state.observe(viewLifecycleOwner) { render(it) }
        when {
            args.apod != null -> sendIntent(ItemIntent.OpenApod(args.apod!!))
            args.date != null -> sendIntent(ItemIntent.OpenDate(args.date.toString()))
            else -> sendIntent(ItemIntent.FetchLatest)
        }
        return binding.root
    }

    override fun render(state: ItemState) {
        with(state) {
            pendingDirection?.let {
                findNavController().navigate(pendingDirection)
                sendIntent(ItemIntent.FollowingDirection)
                return
            }
            apod?.let {
                (activity as ActionBarActivity).setTitle(getString(R.string.single_apod_title, it.date))
                binding.apod = apod
            }
            binding.isLoading = isLoading
            message?.let { renderError(it) }
        }
    }

    private fun renderError(message: ApodMessage) {
        Snackbar.make(coordinatorLayout, message.message, Snackbar.LENGTH_LONG).apply {
            if (message.date.isNotEmpty()) {
                setAction(R.string.button_retry) { sendIntent(ItemIntent.OpenDate(message.date)) }
            }
            show()
        }
    }

    private fun sendIntent(intent: ItemIntent) {
        lifecycleScope.launch {
            itemViewModel.intents.send(intent)
        }
    }

    private fun showDatePicker() {
        val currentDate = binding.apod?.date?.let {
            parser.apodDateToCalendar(it)?.timeInMillis
        }
        MaterialDatePicker.Builder.datePicker()
            .setSelection(currentDate ?: System.currentTimeMillis())
            .setCalendarConstraints(calendarConstraint)
            .build()
            .apply { addOnPositiveButtonClickListener { handleCalendarResult(it) } }
            .show(childFragmentManager, "")
    }

    private fun showSaveDialog() = AlertDialog.Builder(requireActivity())
        .setTitle(R.string.save_dialog_title)
        .setMessage(R.string.save_dialog_message)
        .setNeutralButton(R.string.save_dialog_button_save) { _, _ ->
            GlideApp.with(requireActivity())
                .asBitmap()
                .load(binding.apod?.url)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        sendIntent(ItemIntent.SaveApod(resource))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
        .setNegativeButton(R.string.save_dialog_button_wallpaper) { _, _ ->
            //sendIntent(ItemIntent.SaveApod)
        }
        .setPositiveButton(R.string.save_dialog_button_lockscreen) { _, _ ->
            //sendIntent(ItemIntent.SaveApod)
        }
        .show()

    private fun handleCalendarResult(time: Long) {
        val cal = Calendar.getInstance().apply { timeInMillis = time }
        val targetDate = parser.calendarToApodDate(cal)
        sendIntent(ItemIntent.OpenDate(targetDate))
    }
}