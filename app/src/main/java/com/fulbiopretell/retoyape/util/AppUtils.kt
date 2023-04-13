package com.fulbiopretell.retoyape.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object AppUtils {
    fun isConnected(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networks: Array<Network> = connectivityManager.getAllNetworks()
        var networkInfo: NetworkInfo
        for (mNetwork in networks) {
            networkInfo = connectivityManager.getNetworkInfo(mNetwork)!!
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true
            }
        }
        return false
    }

    fun bitmapDescriptorFromVector(context: Context?, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context!!, vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}