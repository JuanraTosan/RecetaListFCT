package com.example.recetalistfct.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.recetalistfct.R
import com.example.recetalistfct.components.CarruselDeFotos
import com.example.recetalistfct.controller.CarritoController.anadirIngredientes
import com.example.recetalistfct.controller.IngredienteController
import com.example.recetalistfct.controller.RecetaController.obtenerRecetaPorId
import com.example.recetalistfct.model.Ingrediente
import com.example.recetalistfct.model.Receta
import com.google.firebase.auth.FirebaseAuth


/**
 * Pantalla de detalle de una receta.
 *
 * Muestra información detallada sobre la receta seleccionada,
 * -Nombre y descripción
 * -Imagen principal y galería de fotos
 * -Lista de ingredientes necesarios
 * -Botón para añadir todos los ingredientes al carrito
 *
 * Si el usuario es el propietario de la receta, se muestra un botón para editarla.
 *
 * @param navController Controlador de navegación para navegar entre pantallas.
 * @param recetaId ID único de la receta a mostrar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleRecetaScreen(
    navController: NavController,
    recetaId: String
) {
    val context = LocalContext.current
    var receta by remember { mutableStateOf<Receta?>(null) }
    var ingredientes by remember { mutableStateOf<List<Ingrediente>>(emptyList()) }

    var isLoadingIngredients by remember { mutableStateOf(false) }

    // Obtener datos del usuario actual desde Firebase Authentication
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val currentUserId = firebaseUser?.uid

    /**
     * Carga la receta y sus ingredientes cuando cambia el ID
     * Se ejecuta automáticamente al abrir la pantalla.
     */
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
                title = { Text(stringResource(R.string.recipe_detail)) },
                actions = {
                    //Mostrar botón de edición solo si el usuario es el creador de la receta.
                    if (currentUserId == receta?.usuarioId) {
                    IconButton(
                        onClick = {
                            receta?.id?.let {
                                navController.navigate("crearRecetas/$it")
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit_recipe),
                            tint = Color.White
                        )
                    }
                }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        content = { paddingValues ->

            //Si no hay receta cargada aún, mostrar un indicador de carga
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

                    // Item 1: Imagen principal de la receta
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

                    // Item 2: Nombre de la receta
                    item {
                        Text(
                            text = recetaData.nombre,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }

                    // Item 3: Descripción de la receta
                    item {
                        Text(
                            text = recetaData.descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    // Item 4:  Título de la seccion de ingredientes de la receta
                    item {
                        Text(
                            text = stringResource(R.string.ingredients),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 16.dp)
                        )
                    }

                    //Item 5: Mostrar estado de carga o listaa de ingredientes
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
                                text = "${ingrediente.nombre} - ${ingrediente.cantidad} (${ingrediente.unidadMedida})",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        item {
                            Text(
                                text = stringResource(R.string.no_ingredients_available),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 8.dp)
                            )
                        }
                    }

                    //Item 6: Botón para añadir todos los ingredientes al carrito
                    item {
                        Button(
                            onClick = {
                                    anadirIngredientes(*ingredientes.toTypedArray())
                                    Toast.makeText(context, "Ingredientes añadidos a la lista", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(stringResource(R.string.add_all_ingredients_to_shopping_list))
                        }
                    }

                    //Item 7: Galería de fotos adicionales de la receta
                    item {
                        Text(
                            text = stringResource(R.string.gallery_pictures),
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