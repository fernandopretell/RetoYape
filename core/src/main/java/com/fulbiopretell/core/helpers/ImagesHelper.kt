package com.fulbiopretell.retoyape.core.helpers

import android.content.Context
import android.util.DisplayMetrics

class ImagesHelper(private val context: Context) {

    companion object {
        const val MDPI_EXT = "_mdpi"
        const val HDPI_EXT = "_hdpi"
        const val XHDPI_EXT = "_xhdpi"
        const val XXHDPI_EXT = "_xxhdpi"
        const val XXXHDPI_EXT = "_xxxhdpi"
    }

    fun getResolutionURL(url: String) : String {
        return if (url.isNotEmpty()) {
            when (context.resources.displayMetrics.densityDpi) {

                DisplayMetrics.DENSITY_MEDIUM -> updateURLResolution(url, MDPI_EXT)

                DisplayMetrics.DENSITY_HIGH -> updateURLResolution(url, HDPI_EXT)

                DisplayMetrics.DENSITY_XHIGH -> updateURLResolution(url, XHDPI_EXT)

                DisplayMetrics.DENSITY_XXHIGH -> updateURLResolution(url, XXHDPI_EXT)

                DisplayMetrics.DENSITY_XXXHIGH -> updateURLResolution(url, XXXHDPI_EXT)

                else -> url

            }
        } else
            url
    }

    private fun updateURLResolution(url: String, ext: String) : String {
        val dotIndex = url.lastIndexOf(".")
        return "${url.substring(0, dotIndex)}$ext${url.substring(dotIndex)}"
    }

}
