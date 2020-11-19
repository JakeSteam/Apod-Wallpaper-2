package uk.co.jakelee.apodwallpaper.app.work

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.work.*
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.coroutineScope
import org.koin.core.KoinComponent
import org.koin.core.inject
import uk.co.jakelee.apodwallpaper.app.ApodDateParser
import uk.co.jakelee.apodwallpaper.app.NotificationHelper
import uk.co.jakelee.apodwallpaper.app.database.ApodRepository
import uk.co.jakelee.apodwallpaper.app.storage.FileSystemHelper
import uk.co.jakelee.apodwallpaper.app.storage.GlideApp
import uk.co.jakelee.apodwallpaper.ui.item.architecture.ItemIntent
import java.util.concurrent.TimeUnit

class ApodWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
): CoroutineWorker(appContext, workerParams), KoinComponent {

    private val apodRepository: ApodRepository by inject()
    private val apodDateParser: ApodDateParser by inject()
    private val fileSystemHelper: FileSystemHelper by inject()

    override suspend fun doWork() = coroutineScope {
        Log.i("WORK", "Starting work!")
        // check apod
        val apod = apodRepository.getApod(apodDateParser.currentApodDate(), false, null)
        Log.i("WORK", "Apod? ${apod?.title}")
        apod?.let {
            // download
            GlideApp.with(appContext)
                .asBitmap()
                .load(apod.url)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        Log.i("WORK", "Apod downloaded: ${resource.byteCount} bytes")
                        fileSystemHelper.saveImage(resource, apod.date)

                        // set as wallpaper

                        // notify
                        Log.i("WORK", "About to notify...")
                        NotificationHelper(appContext).show(it)
                        Log.i("WORK", "Notified")
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })

        }
        Result.success()
    }

    companion object {
        fun getRegularWorkRequest(): WorkRequest {
            return PeriodicWorkRequestBuilder<ApodWorker>(
                12, TimeUnit.HOURS,
                2, TimeUnit.HOURS)
                .setConstraints(Constraints.Builder().setRequiresStorageNotLow(true).build())
                .build()
        }

        fun getOneOffWorkRequest(): WorkRequest {
            return OneTimeWorkRequestBuilder<ApodWorker>()
                .setInitialDelay(1, TimeUnit.MINUTES)
                .build()
        }
    }
}