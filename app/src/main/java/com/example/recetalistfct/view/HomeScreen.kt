package com.example.recetalistfct.view

import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

    val activity = LocalActivity.current
    ?: throw IllegalStateException("LoginScreen debe estar alojada en una Activity")

    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var fotoPerfil by remember { mutableStateOf("") }

    val recetas = remember { mutableStateListOf<Receta>() }
    val cargando = remember { mutableStateOf(true) }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isMenuOpen by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Detectar el estado del teclado
    val imeVisible = LocalDensity.current.density < 0.5
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route ?: "home"

    val expanded = remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                            items(recetas) { receta ->
                                RecetaCard(
                                    receta = receta,
                                    onClick = {
                                        navController.navigate("detalleRecetas")
                                    }                                   ///${receta.id}
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}