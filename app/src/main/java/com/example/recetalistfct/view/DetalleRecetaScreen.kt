package com.example.recetalistfct.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.recetalistfct.components.CarruselDeFotos
import com.example.recetalistfct.controller.IngredienteController
import com.example.recetalistfct.controller.RecetaController.obtenerRecetaPorId
import com.example.recetalistfct.model.Ingrediente
import com.example.recetalistfct.model.Receta
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleRecetaScreen(
    navController: NavController,
    recetaId: String
) {
    var receta by remember { mutableStateOf<Receta?>(null) }
    var ingredientes by remember { mutableStateOf<List<Ingrediente>>(emptyList()) }
    var isLoadingIngredients by remember { mutableStateOf(false) }

    // Obtener usuario actual
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val currentUserId = firebaseUser?.uid

    // Cargar receta y sus ingredientes
    LaunchedEffect(recetaId) {
        isLoadingIngredients = true
        obtenerRecetaPorId(recetaId) { recetaData ->
            receta = recetaData
            // Obtener los ingredientes asociados a esta receta
            IngredienteController.obtenerIngredientesPorReceta(recetaId) { ingredientesList ->
                ingredientes = ingredientesList
                isLoadingIngredients = false
            }


        }


    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de la Receta") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        content = { paddingValues ->


            if (receta == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val recetaData = receta!!

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {

                    // Imagen principal
                    item {
                        val fotoPrincipal = recetaData.fotoReceta
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = if (fotoPrincipal.isNotBlank()) fotoPrincipal else "https://via.placeholder.com/600x400.png?text=Sin+Imagen "
                            ),
                            contentDescription = "Imagen principal",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .padding(bottom = 16.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Mostrar botón de editar solo si el usuario es el propietario
                    if (currentUserId == recetaData.usuarioId) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = {
                                        navController.navigate("crearRecetas/${recetaData.id}")
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar receta",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    // Nombre
                    item {
                        Text(
                            text = recetaData.nombre,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }

                    // Descripción
                    item {
                        Text(
                            text = recetaData.descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    // Título Ingredientes
                    item {
                        Text(
                            text = "Ingredientes",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 16.dp)
                        )
                    }

                    // Estado de carga / lista de ingredientes
                    if (isLoadingIngredients) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(30.dp))
                            }
                        }
                    } else if (ingredientes.isNotEmpty()) {
                        items(ingredientes) { ingrediente ->
                            Text(
                                text = "${ingrediente.nombre} - ${ingrediente.cantidad}",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        item {
                            Text(
                                text = "No hay ingredientes disponibles para esta receta.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 8.dp)
                            )
                        }
                    }

                    // Galería de Fotos
                    item {
                        Text(
                            text = "Galería de Fotos",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 16.dp)
                        )
                        CarruselDeFotos(recetaData.fotosGaleriaReceta)
                    }
                }
            }
        }
    )
}