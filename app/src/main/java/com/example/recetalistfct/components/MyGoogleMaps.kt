package com.example.recetalistfct.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.recetalistfct.model.Place
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * Componente reutilizable que muestra un mapa de Google Maps
 *
 * Incluye marcadores de:
 * -Ubicación actual del usuario.
 * -Todos los lugares pasados como parámetro.
 *
 * @param location Ubicación actual del usuario.
 * @param places Lista de lugares a mostrar en el mapa.
 * @param cameraPosition Posición opcional de la cámara para centrar el mapa
 * en un lugar específico.
 */
@Composable
fun MyGoogleMaps(
    location: LatLng,
    places: List<Place>,
    cameraPosition: CameraPosition?
){
    //Estado de la posición de la cámara del mapa
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 15f)
    }

    // Actualiza la cámara si se selecciona un nuevo lugar desde la lista de tarjetas
    if (cameraPosition != null) {
        cameraPositionState.position = cameraPosition
    }

    //Vista del mapa de Google Maps
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(zoomControlsEnabled = false)
    ) {
        // Marcador de la ubicación actual del usuario
        Marker(
            state = MarkerState(position = location)
        ) {
            it.title = "Mi ubicación"
        }

        // Marcadores de todos los lugares guardados.
        places.forEach { place ->
            Marker(
                state = MarkerState(position = LatLng(place.lat, place.lon)),
                title = place.name
            )
        }
    }
}