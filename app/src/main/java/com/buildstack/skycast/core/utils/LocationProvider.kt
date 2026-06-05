package com.buildstack.skycast.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    @SuppressLint("MissingPermission") // We assume permissions are requested in UI
    suspend fun getCurrentLocation(): Location? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        
        if (!isGpsEnabled && !isNetworkEnabled) {
            return null
        }

        val provider = if (isGpsEnabled) LocationManager.GPS_PROVIDER else LocationManager.NETWORK_PROVIDER

        return suspendCancellableCoroutine { cont ->
            val locationListener = android.location.LocationListener { location ->
                if (cont.isActive) {
                    cont.resume(location)
                }
            }

            try {
                // Get last known location for faster response
                val lastKnown = locationManager.getLastKnownLocation(provider)
                if (lastKnown != null) {
                    cont.resume(lastKnown)
                    return@suspendCancellableCoroutine
                }

                locationManager.requestSingleUpdate(provider, locationListener, Looper.getMainLooper())
                
                cont.invokeOnCancellation {
                    locationManager.removeUpdates(locationListener)
                }
            } catch (e: Exception) {
                if (cont.isActive) {
                    cont.resume(null)
                }
            }
        }
    }
}
