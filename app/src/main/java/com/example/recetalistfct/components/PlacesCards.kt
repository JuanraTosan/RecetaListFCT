package com.example.recetalistfct.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recetalistfct.controller.PlaceController.RatingStars
import com.example.recetalistfct.model.Place
import com.google.android.gms.maps.model.LatLng

@Composable
fun PlacesCardList(
    places: List<Place>,
    onPlaceClick: (LatLng) -> Unit
) {
    if (places.isEmpty()) {
        Text("No hay lugares guardados")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(places) { place ->
            PlaceCard(place = place) {
                onPlaceClick(LatLng(place.lat, place.lon))
            }
        }
    }
}

@Composable
fun PlaceCard(place: Place, onClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp) // Altura mínima, se expandirá dinámicamente
            .animateContentSize() // Animación al cambiar tamaño
            .clickable {
                expanded = !expanded
                onClick()
                       },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (place.type == "supermercado") Icons.Default.ShoppingCart else Icons.Default.Event,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column {// Contenido que solo aparece cuando está contraido
                if (!expanded) {
                    Text(text = place.name, fontSize = 16.sp)
                    Text(text = place.type, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(10.dp))
                    RatingStars(rating = place.rating)

                } else { // Contenido adicional que solo aparece cuando está expandido
                    Text(text = place.name, fontSize = 16.sp)
                    Text(text = place.type, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Descripción: ${place.descripcion.take(100)}${if (place.descripcion.length > 100) "..." else ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "🕒 ${place.horaApertura} - ${place.horaCierre}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    RatingStars(rating = place.rating)
                }
            }
        }
    }
}