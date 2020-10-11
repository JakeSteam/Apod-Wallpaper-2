package uk.co.jakelee.apodwallpaper.app.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.jakelee.apodwallpaper.model.Apod

@Dao
interface ApodDao {

    @Query("SELECT * FROM apods ORDER BY date DESC")
    fun getAll(): LiveData<List<Apod>>

    @Query("SELECT * FROM apods ORDER BY date DESC")
    fun getAllPaged(): DataSource.Factory<Int, Apod>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(apods: List<Apod>)
}