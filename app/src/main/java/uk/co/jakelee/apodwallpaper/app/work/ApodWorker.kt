package uk.co.jakelee.apodwallpaper.app.work

import android.content.Context
import android.util.Log
import androidx.work.*
import kotlinx.coroutines.coroutineScope
import org.koin.core.KoinComponent
import org.koin.core.inject
import uk.co.jakelee.apodwallpaper.app.ApodDateParser
import uk.co.jakelee.apodwallpaper.app.NotificationHelper
import uk.co.jakelee.apodwallpaper.app.database.ApodRepository
import java.util.concurrent.TimeUnit

class ApodWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
): CoroutineWorker(appContext, workerParams), KoinComponent {

    private val apodRepository: ApodRepository by inject()
    private val apodDateParser = ApodDateParser()

    override suspend fun doWork() = coroutineScope {
        Log.i("WORK", "Starting work!")
        // check apod
        val apod = apodRepository.getApod(apodDateParser.currentApodDate(), false, null)
        Log.i("WORK", "Apod? ${apod?.title}")
        apod?.let {
            // download

            // set as wallpaper

            // notify
            NotificationHelper(appContext).show(it)
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