package com.example.recetalistfct

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.google.firebase.auth.FirebaseAuth

data class Receta(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val autorId: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController
) {
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val userId = firebaseUser?.uid

    if (userId == null){
        Log.w("HomeScreen", "Usuario no autenticado.")
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isMenuOpen by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Detectar el estado del teclado
    val imeVisible = LocalDensity.current.density < 0.5

    Log.d("HomeScreen", "Id de usuario ${userId}")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit){
                detectTapGestures ( onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    title = {
                        Column {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                label = { Text("Buscar receta...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 8.dp)
                                    .height(56.dp),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                                    focusedContainerColor = Color.White.copy(alpha = 0.3f),
                                    unfocusedTextColor = Color.Black,
                                    focusedTextColor = Color.Black,
                                    unfocusedPlaceholderColor = Color.Black.copy(alpha = 0.6f),
                                    focusedPlaceholderColor = Color.Black.copy(alpha = 0.8f),
                                )
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isMenuOpen = !isMenuOpen }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Configuración")
                        }
                    },
                )
            },
            floatingActionButton = {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    // Botón de Eventos Gastronómicos
                    FloatingActionButton(
                        onClick = {
                            navController.navigate("events") // Navegar a la pantalla de eventos
                        },
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                    ) {
                        Icon(Icons.Default.Place, contentDescription = "Eventos Gastronómicos")
                    }

                    // Botón de Mis Recetas
                    FloatingActionButton(
                        onClick = { navController.navigate("mis_recetas/$userId") },
                    ) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Mis Recetas")
                    }
                }
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(bottom = if (imeVisible) 250.dp else 0.dp)  // Ajuste para cuando el teclado está visible
                ) {
                    Text(
                        text = "Lista de Recetas",
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp)
                    ) {
                        /*
                        if (searchQuery.isEmpty()) { // Si no escribimos nada en la busqueda muestra esta:
                            items(recetas) {receta ->
                                RecipeCard(
                                    receta = receta,
                                    onClick = {
                                        navController.navigate("recipe_edit/${receta.id}")
                                    }
                                )
                            }
                        } else if (filteredRecetas.isNotEmpty()) { //Si escribimos algo en la busqueda muestra esta:
                            items(filteredRecetas) { receta ->
                                RecipeCard(
                                    receta = receta,
                                    onClick = {
                                        navController.navigate("recipe_edit/${receta.id}")
                                    }
                                )
                            }
                        } else {
                            item {
                                Text(
                                    text = "No se encontraron recetas con ese nombre.",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }*/
                    }

                }
            }
        )
        /*
        PreferencesSettingsMenu(
            isMenuOpen = isMenuOpen,
            onCloseMenu = { isMenuOpen = false },
            darkModeEnabled = darkModeEnabled,
            onDarkModeToggle = onDarkModeToggle,
            navController = navController
        )
         */
    }
}