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
import androidx.compose.ui.res.stringResource
import com.example.recetalistfct.R

@Composable
fun BottomBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedItem == "map",
            onClick = { onItemSelected("map") },
            icon = { Icon(Icons.Filled.Place, contentDescription = stringResource(R.string.map)) },
            label = { Text(stringResource(R.string.map)) }
        )
        NavigationBarItem(
            selected = selectedItem == "carrito",
            onClick = { onItemSelected("carrito") },
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = stringResource(R.string.cart)) },
            label = { Text(stringResource(R.string.cart)) }
        )
        NavigationBarItem(
            selected = selectedItem == "home",
            onClick = { onItemSelected("home") },
            icon = { Icon(Icons.Filled.Home, contentDescription = stringResource(R.string.home)) },
            label = { Text(stringResource(R.string.home)) }
        )
        NavigationBarItem(
            selected = selectedItem == "recetas",
            onClick = { onItemSelected("recetas") },
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = stringResource(R.string.my_recipes)) },
            label = { Text(stringResource(R.string.my_recipes)) }
        )
        NavigationBarItem(
            selected = selectedItem == "perfil",
            onClick = { onItemSelected("perfil") },
            icon = { Icon(Icons.Filled.Person, contentDescription = stringResource(R.string.profile)) },
            label = { Text(stringResource(R.string.profile)) }
        )
    }
}
