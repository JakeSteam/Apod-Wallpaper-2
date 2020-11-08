package uk.co.jakelee.apodwallpaper.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import uk.co.jakelee.apodwallpaper.MainActivity
import uk.co.jakelee.apodwallpaper.R
import uk.co.jakelee.apodwallpaper.model.Apod

// TODO: Proof of concept
class NotificationHelper(val context: Context) {

    fun show(apod: Apod) {
        createNotificationChannel()
        val notif = NotificationCompat.Builder(context, "APOD")
            .setContentTitle(apod.title)
            .setContentText(apod.explanation)
            .setStyle(NotificationCompat.BigTextStyle().bigText(apod.explanation))
            .setSmallIcon(R.drawable.ic_calendar)
            .setAutoCancel(true)
            .setContentIntent(PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0))
            .build()
        NotificationManagerCompat.from(context).notify(41242, notif)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("APOD", "APOD", importance).apply {
                description = "APOD NOTIFICATIONS"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}