package uk.co.jakelee.apodwallpaper.extensions

import java.io.File

data class FolderInfo(val bytes: Long, val files: Int)

fun File.getFolderInfo(): FolderInfo {
    val allFiles = walkTopDown().filter { it.isFile }.map { it.length() }
    return FolderInfo(allFiles.sum(), allFiles.count())
}