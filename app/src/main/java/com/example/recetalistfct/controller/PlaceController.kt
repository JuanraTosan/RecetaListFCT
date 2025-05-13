package com.example.recetalistfct.controller

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.recetalistfct.model.Place
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

object PlaceController {

    @Composable
    fun rememberPlaces(): List<Place> {
        val places = remember { mutableStateListOf<Place>() }
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            val database = Firebase.database
            val placesRef = database.getReference("place")

            placesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    places.clear()
                    for (placeSnapshot in snapshot.children) {
                        val place = placeSnapshot.getValue(Place::class.java)
                        place?.let { places.add(it) }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error al cargar lugares", Toast.LENGTH_SHORT).show()
                }
            })
        }

        return places
    }


    @Composable
    fun RatingStars(rating: Double) {
        Row {
            val fullStars = rating.toInt()
            val hasHalfStar = rating % 1 >= 0.5

            (1..5).forEach { index ->
                when {
                    index <= fullStars -> Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow)
                    index == fullStars + 1 && hasHalfStar -> Icon(Icons.AutoMirrored.Filled.StarHalf, contentDescription = null, tint = Color.Yellow)
                    else -> Icon(Icons.Default.StarOutline, contentDescription = null, tint = Color.LightGray)
                }
            }
        }
    }
}