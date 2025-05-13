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
import com.example.recetalistfct.components.PlacesCardList
import com.example.recetalistfct.controller.PlaceController.rememberPlaces
import com.example.recetalistfct.model.Place
import com.example.recetalistfct.utils.getCurrentLocation
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(navController: NavController) {
    val context = LocalContext.current
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var cameraPosition by remember { mutableStateOf<CameraPosition?>(null) }

    // Estado para posición del marcador del mapa
    val places = rememberPlaces()

    // Para manejar el lanzamiento del permiso
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // Permiso concedido, obtenemos la ubicación
            getCurrentLocation(context) { lat, lon ->
                currentLocation = LatLng(lat, lon)
            }
        } else {
            // Permiso denegado
            Toast.makeText(context, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Pide el permiso si no está concedido
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (places.isNotEmpty()) {
                    PlacesCardList(places = places) { latLng ->
                        cameraPosition = CameraPosition.fromLatLngZoom(latLng, 15f)
                    }
                } else {
                    Text("No hay lugares disponibles")
                }
            }
        }
    }
}

@Composable
fun MyGoogleMaps(
    location: LatLng,
    places: List<Place>,
    cameraPosition: CameraPosition?
){
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 15f)
    }

    // Cambia la cámara si se selecciona un lugar
    if (cameraPosition != null) {
        cameraPositionState.position = cameraPosition
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(zoomControlsEnabled = false)
    ) {
        // Marcador de mi ubicación
        Marker(
            state = MarkerState(position = location)
        ) {
            it.title = "Mi ubicación"
        }

        // Marcadores de los lugares guardados
        places.forEach { place ->
            Marker(
                state = MarkerState(position = LatLng(place.lat, place.lon)),
                title = place.name
            )
        }
    }
}