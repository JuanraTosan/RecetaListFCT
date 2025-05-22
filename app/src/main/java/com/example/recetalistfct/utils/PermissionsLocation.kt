package com.example.recetalistfct.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

fun hasLocationPermissions(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    return fine == PackageManager.PERMISSION_GRANTED && coarse == PackageManager.PERMISSION_GRANTED
}

/**
 * Obtiene la última ubicación conocida del dispositivo.
 *
 * Si la ubicación está disponible, llama al callback `onLocationReceived` con las coordenadas.
 *
 * @param context Contexto desde el cual se solicita la ubicación.
 * @param onLocationReceived Callback que recibe la latitud y longitud como parámetros.
 */
fun getCurrentLocation(context: Context, onLocationReceived: (latitude: Double, longitude: Double) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    try {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    onLocationReceived(it.latitude, it.longitude)
                }
            }
    } catch (e: SecurityException) {
        e.printStackTrace()
        }
}