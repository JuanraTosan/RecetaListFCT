package com.example.recetalistfct.view

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.recetalistfct.components.BottomBar
import com.example.recetalistfct.components.MyGoogleMaps
import com.example.recetalistfct.components.PlacesCardList
import com.example.recetalistfct.controller.PlaceController.rememberPlaces
import com.example.recetalistfct.utils.getCurrentLocation
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

/**
 * Pantalla principal del mapa.
 *
 * Muestra:
 * - Un mapa interactivo centrado en la ubicación actual del usuario.
 * - Marcadores de lugares predefinidos o guardados.
 * - Una lista de tarjetas con información de los lugares disponibles.
 *
 * Requiere permiso de ubicación para funcionar correctamente.
 *
 * @param navController Controlador de navegación para cambiar entre pantallas.
 */
@Composable
fun MapScreen(navController: NavController) {
    val context = LocalContext.current
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var cameraPosition by remember { mutableStateOf<CameraPosition?>(null) }

    // Lista de lugares (sitios predefinidos o guardados por el usuario)
    val places = rememberPlaces()

    // Launcher para solicitar permiso de ubicación al usuario
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // Permiso concedido, obtenemos la ubicación
            getCurrentLocation(context) { lat, lon ->
                currentLocation = LatLng(lat, lon)
            }
        } else {
            // Permiso denegado, se muestra mensaje de error
            Toast.makeText(context, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Solicita el permiso de ubicación cuando se entra a esta pantalla.
     * Se ejecuta una única vez gracias a 'LaunchedEffect(Unit)'
     */
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Scaffold(
        bottomBar = {
            BottomBar(selectedItem = "map") { navController.navigate(it) }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            //Zona superior: Mapa interactivo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (currentLocation != null) {
                    MyGoogleMaps(location = currentLocation!!,
                        places = places,
                        cameraPosition = cameraPosition
                    )
                } else {
                    Text("Obteniendo ubicación...")
                }
            }

            //Zona inferior: Lista de lugares como tarjetas desplezables
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (places.isNotEmpty()) {
                    PlacesCardList(places = places) { latLng ->
                        //Al pulsar en una tarjeta, mueve la cámara del mapa a esa ubicación
                        cameraPosition = CameraPosition.fromLatLngZoom(latLng, 15f)
                    }
                } else {
                    Text("No hay lugares disponibles")
                }
            }
        }
    }
}


