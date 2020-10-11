package uk.co.jakelee.apodwallpaper.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apods")
data class Apod(
    @PrimaryKey
    val date: String,
    val url: String,
    val title: String
)