package com.example.recetalistfct.view

import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.recetalistfct.components.BottomBar
import com.example.recetalistfct.components.FastSettings
import com.example.recetalistfct.components.FiltrosReceta
import com.example.recetalistfct.components.RecetaCard
import com.example.recetalistfct.controller.RecetaController.obtenerTodasLasRecetas
import com.example.recetalistfct.controller.UsuarioController.obtenerUsuario
import com.example.recetalistfct.model.Receta
import com.example.recetalistfct.model.Usuario
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val userId = firebaseUser?.uid

    val activity = LocalActivity.current
    ?: throw IllegalStateException("LoginScreen debe estar alojada en una Activity")

    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var fotoPerfil by remember { mutableStateOf("") }

    val recetas = remember { mutableStateListOf<Receta>() }
    val cargando = remember { mutableStateOf(true) }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isMenuOpen by remember { mutableStateOf(false) }
    var mostrarFiltros by remember { mutableStateOf(false) }
    val recetasFiltradasPorBusqueda = remember(searchQuery) {
        derivedStateOf {
            if (searchQuery.isBlank() || searchQuery == "") {
                // Si no hay texto, mostramos todas
                recetas
            } else {
                // Buscamos por nombre ignorando mayúsculas/minúsculas
                recetas.filter { receta ->
                    receta.nombre.contains(searchQuery, ignoreCase = true)
                }
            }
        }
    }

    val focusManager = LocalFocusManager.current

    // Detectar el estado del teclado
    val imeVisible = LocalDensity.current.density < 0.5
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route ?: "home"

    val expanded = remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (userId == null) {
        Log.w("HomeScreen", "Usuario no autenticado.")
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Usuario no autenticado. Por favor, inicie sesión.")
        }
        return
    }

    Log.d("HomeScreen", "Id de usuario $userId")

    LaunchedEffect(userId) {
        obtenerUsuario(userId) { fetchedUser ->
            fetchedUser?.let {
                usuario = it
                fotoPerfil = it.fotoPerfil
            }
        }

        obtenerTodasLasRecetas { todasRecetas ->
            recetas.clear()
            recetas.addAll(todasRecetas.sortedByDescending { it.fechaCreacion })
            cargando.value = false
        }
    }
    FastSettings(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        navController = navController,
        context = context,
        activity = activity
    )

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
            bottomBar = {
                BottomBar(selectedItem = currentDestination) { selected ->
                    when (selected) {
                        "map" -> navController.navigate("map")
                        "carrito" -> navController.navigate("carrito")
                        "home" -> navController.navigate("home")
                        "recetas" -> navController.navigate("recetas")
                        "perfil" -> navController.navigate("perfil")
                    }
                }
            },
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
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                )
                            )
                        }
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { isMenuOpen = true }) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(fotoPerfil)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier
                                        .size(250.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            FastSettings(
                                expanded = isMenuOpen,
                                onDismissRequest = { isMenuOpen = false},
                                navController = navController,
                                context = context,
                                activity = activity
                                )
                        }
                    }
                )
            },
            content = { padding ->
                if (cargando.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(bottom = if (imeVisible) 250.dp else 0.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Lista de Recetas",
                                style = MaterialTheme.typography.titleLarge
                            )

                            IconButton(onClick = { mostrarFiltros = !mostrarFiltros }) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Filtrar"
                                )
                            }
                        }

                        if (mostrarFiltros) {
                            FiltrosReceta(
                                navController = navController,
                                onApplyFilter = { filtro ->
                                    val filteredList = recetasFiltradasPorBusqueda.value.filter { receta ->
                                        val matchesTipoComida =
                                            filtro.tipoComida.isEmpty() || receta.tipoComida.equals(filtro.tipoComida, ignoreCase = true)

                                        val matchesDificultad =
                                            filtro.dificultad.isEmpty() || receta.dificultad.equals(filtro.dificultad, ignoreCase = true)

                                        val matchesTiempo =
                                            filtro.tiempoMin <= 0 || receta.tiempoPreparacionMin <= filtro.tiempoMin

                                        matchesTipoComida && matchesDificultad && matchesTiempo
                                    }

                                    // Actualizamos la lista global con el resultado combinado
                                    recetas.clear()
                                    recetas.addAll(filteredList)
                                }
                            )
                        }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp)
                        ) {
                            items(recetasFiltradasPorBusqueda.value) { receta ->
                                RecetaCard(
                                    receta = receta,
                                    onClick = {
                                        navController.navigate("detalleRecetas/${receta.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}