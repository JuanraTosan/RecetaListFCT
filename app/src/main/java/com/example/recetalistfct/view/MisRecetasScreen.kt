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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.recetalistfct.model.Receta
import com.example.recetalistfct.components.BottomBar
import com.example.recetalistfct.components.ConfirmDeleteDialog
import com.example.recetalistfct.controller.RecetaController.eliminarReceta
import com.example.recetalistfct.controller.RecetaController.obtenerRecetasDeUsuario
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MisRecetasScreen(navController: NavController) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

    var recetas by remember { mutableStateOf<List<Receta>>(emptyList()) }
    var recetaAEliminar by remember { mutableStateOf<Receta?>(null) }
    val offsetMap = remember { mutableStateMapOf<String, Float>() }

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
                Text("Mis Recetas", style = MaterialTheme.typography.headlineMedium)

                Spacer(Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recetas, key = { it.id }) { receta ->
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
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(8.dp)
                            ) {
                                Box {
                                    Image(
                                        painter = rememberAsyncImagePainter(receta.fotoReceta),
                                        contentDescription = receta.nombre,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    val alpha = ((-(offsetMap[receta.id] ?: 0f)) / 300f).coerceIn(0f, 1f)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Red.copy(alpha = alpha))
                                    )
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

                Button(
                    onClick = { navController.navigate("crearRecetas") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Text("Añadir Receta")
                }
            }

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