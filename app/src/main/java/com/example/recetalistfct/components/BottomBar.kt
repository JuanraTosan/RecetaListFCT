package com.example.recetalistfct.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place

@Composable
fun BottomBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedItem == "map",
            onClick = { onItemSelected("map") },
            icon = { Icon(Icons.Filled.Place, contentDescription = "Mapa") },
            label = { Text("Mapa") }
        )
        NavigationBarItem(
            selected = selectedItem == "carrito",
            onClick = { onItemSelected("carrito") },
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Lista de Compra") },
            label = { Text("Carrito") }
        )
        NavigationBarItem(
            selected = selectedItem == "home",
            onClick = { onItemSelected("home") },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = selectedItem == "recetas",
            onClick = { onItemSelected("recetas") },
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Mis Recetas") },
            label = { Text("Mis recetas") }
        )
        NavigationBarItem(
            selected = selectedItem == "perfil",
            onClick = { onItemSelected("perfil") },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}
