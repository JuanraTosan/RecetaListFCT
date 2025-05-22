package com.example.recetalistfct.view

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.recetalistfct.R
import com.example.recetalistfct.model.Receta
import com.example.recetalistfct.components.BottomBar
import com.example.recetalistfct.components.ConfirmDeleteDialog
import com.example.recetalistfct.controller.RecetaController.eliminarReceta
import com.example.recetalistfct.controller.RecetaController.obtenerRecetasDeUsuario
import com.google.firebase.auth.FirebaseAuth


/**
 * Pantalla que muestra recetas creadas por el usuario actual.
 *
 * Caracteristicas:
 * - Lista desplazable de recetas
 * - Acceso rápido a edición al pulsar en una receta
 * - Eliminar una receta al deslizar hacia la izuierda.
 * - Botón para crear nuevas recetas.
 * - Confirmación antes de eliminar una receta.
 *
 * @param navController Controlador de navegación para cambiar entre pantallas.
 */
@Composable
fun MisRecetasScreen(navController: NavController) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

    var recetas by remember { mutableStateOf<List<Receta>>(emptyList()) }
    var recetaAEliminar by remember { mutableStateOf<Receta?>(null) }
    val offsetMap = remember { mutableStateMapOf<String, Float>() }

    /**
     * Carga todas las recetas asociadas al usuario actual desde Firebase.
     * Se ejecuta al iniciar la pantalla.
     */
    obtenerRecetasDeUsuario(uid) { recetasCargadas ->
            recetas = recetasCargadas.sortedByDescending { it.fechaCreacion }
        }

    Scaffold(
        bottomBar = {
            BottomBar(selectedItem = "recetas") { navController.navigate(it) }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                //Titulo de la pantalla
                Text(stringResource(R.string.my_recipes), style = MaterialTheme.typography.headlineMedium)

                Spacer(Modifier.height(16.dp))

                //Lista vertical de recetas usando LazyColumn
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recetas, key = { it.id }) { receta ->
                        //Animación suave de desplazamiento horizontal
                        val offsetX by animateDpAsState(
                            targetValue = (offsetMap[receta.id] ?: 0f).dp,
                            label = "offset"
                        )


                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(x = offsetX)
                                .pointerInput(receta.id) {
                                    detectHorizontalDragGestures(
                                        onHorizontalDrag = { _, dragAmount ->
                                            if (dragAmount < 0) { // Solo permitir hacia la izquierda
                                                val newOffset = (offsetMap[receta.id] ?: 0f) + dragAmount
                                                offsetMap[receta.id] = newOffset
                                                if (newOffset < -300f && recetaAEliminar == null) {
                                                    recetaAEliminar = receta
                                                }
                                            }
                                        },
                                        onDragEnd = {
                                            if (recetaAEliminar == null) {
                                                offsetMap[receta.id] = 0f // Si no se estaba eliminando, vuelve
                                            }
                                        }
                                    )
                                }
                        ) {
                            //Tarjeta individual de receta
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(8.dp),
                                onClick = { navController.navigate("crearRecetas/${receta.id}") }
                            ) {
                                Box {
                                    //Imagen principal de la receta
                                    Image(
                                        painter = rememberAsyncImagePainter(receta.fotoReceta),
                                        contentDescription = receta.nombre,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    //Capa roja semi-transparente que aparece al deslizar receta para eliminar
                                    val alpha = ((-(offsetMap[receta.id] ?: 0f)) / 300f).coerceIn(0f, 1f)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Red.copy(alpha = alpha))
                                    )
                                    //Nombre de la receta en la parte inferior
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.Black.copy(alpha = 0.5f))
                                            .align(Alignment.BottomStart)
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            receta.nombre,
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                //Botón para crear nueva receta
                Button(
                    onClick = { navController.navigate("crearRecetas") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Text(stringResource(R.string.add_recipe))
                }
            }

            //Diálogo de confirmación de eliminación (si hay una receta seleccionada)
            recetaAEliminar?.let { receta ->
                ConfirmDeleteDialog(
                    recetaNombre = receta.nombre,
                    onConfirm = {
                        eliminarReceta(receta.id) { exito ->
                            if (exito) {
                                recetas = recetas.filter { it.id != receta.id }
                                Toast.makeText(context, "Receta eliminada correctamente", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al eliminar la receta", Toast.LENGTH_SHORT).show()
                            }
                        }
                        recetaAEliminar = null
                        offsetMap.remove(receta.id)
                    },
                    onDismiss = {
                        recetaAEliminar = null
                        offsetMap[receta.id] = 0f
                    }
                )
            }
        }
    }
}