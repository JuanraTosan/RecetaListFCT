package com.example.recetalistfct.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

fun hasLocationPermissions(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    return fine == PackageManager.PERMISSION_GRANTED && coarse == PackageManager.PERMISSION_GRANTED
}

/*
fun requestLocationPermissions(context: Context) {
    ActivityCompat.requestPermissions(
        context as ComponentActivity,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
        REQUEST_LOCATION_PERMISSION
    )
}

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