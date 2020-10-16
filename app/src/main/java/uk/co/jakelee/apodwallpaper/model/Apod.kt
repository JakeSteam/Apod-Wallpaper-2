package uk.co.jakelee.apodwallpaper.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import retrofit2.http.Field

@Parcelize
@Entity(tableName = "apods")
data class Apod(
    @PrimaryKey val date: String,
    val title: String,
    val explanation: String,
    @Field("media_type") val mediaType: String,
    val url: String,
    @Field("hdurl") val urlLarge: String?,
    val copyright: String?
): Parcelable {

    fun isImage() = mediaType == "image"

}