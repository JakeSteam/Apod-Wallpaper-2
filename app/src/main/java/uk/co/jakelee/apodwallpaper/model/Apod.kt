package uk.co.jakelee.apodwallpaper.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "apods")
data class Apod(
    @PrimaryKey val date: String,
    val title: String,
    val explanation: String,
    val media_type: String,
    val url: String,
    val hdurl: String?,
    val copyright: String?
): Parcelable {

    fun isImage() = media_type == "image"

}