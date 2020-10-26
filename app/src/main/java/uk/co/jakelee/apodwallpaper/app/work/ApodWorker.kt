package uk.co.jakelee.apodwallpaper.app.work

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

class ApodWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        Log.i("WORK", "Starting work!")
        // check apod
        // download apod to file
        // set as wallpaper
        // notify

        // Indicate whether the work finished successfully with the Result
        return Result.success()
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
                .setInitialDelay(2, TimeUnit.MINUTES)
                .build()
        }
    }
}