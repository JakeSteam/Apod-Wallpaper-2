package uk.co.jakelee.apodwallpaper.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uk.co.jakelee.apodwallpaper.model.Apod

@Database(entities = [Apod::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun apodDao(): ApodDao

    companion object {
        fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "AppDatabase")
                .fallbackToDestructiveMigration()
                .build()
    }
}