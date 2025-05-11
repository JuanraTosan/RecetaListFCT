package com.example.recetalistfct.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.example.recetalistfct.R
import com.example.recetalistfct.components.BottomBar

import com.google.android.gms.maps.UiSettings
/*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest

 */
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings


import kotlinx.coroutines.delay
import kotlin.math.sin
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember

@Composable
fun MapScreen(navController: NavController) {

    val context = LocalContext.current
    //val placesClient = remember { Places.createClient(context) }
    val scope = rememberCoroutineScope()
    //var placesList by remember { mutableStateOf(listOf<String>()) }

    /*
        var hasLocationPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
        }

        // Launcher para pedir permiso
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            hasLocationPermission = isGranted
        }

        // Pedir permiso al entrar si no está concedido
        LaunchedEffect(Unit) {
            if (!hasLocationPermission) {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        LaunchedEffect(Unit) {
            val placeFields = listOf(Place.Field.DISPLAY_NAME, Place.Field.FORMATTED_ADDRESS)
            val request = FindCurrentPlaceRequest.newInstance(placeFields)

            val task = placesClient.findCurrentPlace(request)
            task.addOnSuccessListener { response ->
                placesList = response.placeLikelihoods.map { it.place.name ?: "Sin nombre" }
            }.addOnFailureListener {
                placesList = listOf("No se pudo cargar lugares")
            }
        }

        */


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
            // Mapa
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            )
// Lista de lugares si hay permiso
            //if (hasLocationPermission) {
                //PlacesList() // aquí puedes poner tu lógica de Places API
            //} else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Estamos trabajando en ello...")
                }
            }
        }
    }
//}