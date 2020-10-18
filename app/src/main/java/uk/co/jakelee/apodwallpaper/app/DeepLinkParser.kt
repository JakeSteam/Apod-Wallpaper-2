package uk.co.jakelee.apodwallpaper.app

import android.content.Intent
import android.net.Uri

class DeepLinkParser(val intent: Intent?) {

    enum class LinkActions(val action: String) {
        DAY("day"),
        BROWSE("browse"),
        SETTINGS("settings"),
        MORE("more")
    }

    data class ParsedLink(val action: LinkActions, val parameter: String?)

    fun parse(): ParsedLink? {
        val action = intent?.action
        val scheme = intent?.scheme
        val data = intent?.data
        if (action != Intent.ACTION_VIEW || data == null) return null

        val isCustomScheme = scheme == APOD_SCHEMA
        val linkAction = getLinkAction(isCustomScheme, data) ?: return null
        val linkParameter = getLinkParameter(isCustomScheme, data)
        return ParsedLink(linkAction, linkParameter)
    }

    private fun getLinkAction(isCustomScheme: Boolean, link: Uri): LinkActions? {
        val actionString = if (isCustomScheme) link.host else link.pathSegments.getOrNull(0)
        return LinkActions.values().firstOrNull { it.action == actionString }
    }

    private fun getLinkParameter(isCustomScheme: Boolean, link: Uri): String? {
        return link.pathSegments.getOrNull(if (isCustomScheme) 0 else 1)
    }

    companion object {
        const val APOD_SCHEMA = "apod"
    }
}