// view/screens/CrearRecetaScreen.kt
package com.example.recetalistfct.view

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.recetalistfct.controller.IngredienteController
import com.example.recetalistfct.controller.RecetaController
import com.example.recetalistfct.model.Ingrediente
import com.example.recetalistfct.model.Receta
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

@Composable
fun CrearRecetaScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: return

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    var ingredientes by remember { mutableStateOf(mutableListOf<Ingrediente>()) }
    var nombreIngrediente by remember { mutableStateOf("") }
    var cantidadIngrediente by remember { mutableStateOf("") }

    var showReplaceDialog by remember { mutableStateOf(false) }
    var ingredienteDuplicado by remember { mutableStateOf<Ingrediente?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imagenUri = uri
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Crear Nueva Receta", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imagenUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imagenUri),
                        contentDescription = "Imagen de la receta",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Seleccionar imagen",
                        tint = Color.White
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre de la receta") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))
            Text("Ingredientes", style = MaterialTheme.typography.titleMedium)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = nombreIngrediente,
                    onValueChange = { nombreIngrediente = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = cantidadIngrediente,
                    onValueChange = { cantidadIngrediente = it },
                    label = { Text("Cantidad") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    if (nombreIngrediente.isNotBlank() && cantidadIngrediente.isNotBlank()) {
                        val existe = ingredientes.any { it.nombre.equals(nombreIngrediente, ignoreCase = true) }


                        if (!existe) {
                            val nuevoIngrediente = Ingrediente(
                                id = UUID.randomUUID().toString(),
                                nombre = nombreIngrediente,
                                cantidad = cantidadIngrediente
                            )
                            ingredientes.add(nuevoIngrediente)
                            nombreIngrediente = ""
                            cantidadIngrediente = ""
                        } else {
                            ingredienteDuplicado = Ingrediente(
                                id = UUID.randomUUID().toString(),
                                nombre = nombreIngrediente,
                                cantidad = cantidadIngrediente
                            )
                            showReplaceDialog = true
                        }
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir ingrediente")
                }
            }

            Spacer(Modifier.height(8.dp))

// Mostrar ingredientes añadidos
            ingredientes.forEachIndexed { index, ingrediente ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text("• ${ingrediente.nombre} - ${ingrediente.cantidad}", modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        IngredienteController.eliminarIngrediente(ingrediente.id) { success ->
                            if (success) {
                                ingredientes.removeAt(index)
                            } else {
                                Toast.makeText(context, "Error al eliminar ingrediente", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar ingrediente")
                    }
                }
            }

            Button(
                onClick = {
                    if (nombre.isNotBlank() && descripcion.isNotBlank() && imagenUri != null) {
                        isUploading = true
                        RecetaController.subirImagenReceta(imagenUri!!, uid) { fotoUrl ->
                            if (fotoUrl != null) {

                                val recetaId = UUID.randomUUID().toString()
                                // Añadir el ID de la receta a los ingredientes
                                val ingredientesConReceta = ingredientes.map {
                                    it.copy(recetaIds = it.recetaIds + recetaId)
                                }

                                // Actualizar en base de datos
                                ingredientesConReceta.forEach { ingredienteActualizado ->
                                    IngredienteController.guardarIngrediente(ingredienteActualizado) {}
                                }

                                val receta = Receta(
                                    id = UUID.randomUUID().toString(),
                                    nombre = nombre,
                                    descripcion = descripcion,
                                    fotoReceta = fotoUrl,
                                    usuarioId = uid,
                                    ingredientes = ingredientes.toList()
                                )
                                RecetaController.guardarReceta(receta) { success ->
                                    isUploading = false
                                    if (success) {
                                        Toast.makeText(context, "Receta guardada!", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack() // Vuelve atrás
                                    } else {
                                        Toast.makeText(context, "Error al guardar receta", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                isUploading = false
                                Toast.makeText(context, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isUploading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isUploading) "Guardando..." else "Guardar Receta")
            }
        }

        // Diálogo de reemplazo
        if (showReplaceDialog && ingredienteDuplicado != null) {
            AlertDialog(
                onDismissRequest = { showReplaceDialog = false },
                title = { Text("Ingrediente duplicado") },
                text = { Text("Ya existe un ingrediente con ese nombre. ¿Deseas reemplazarlo?") },
                confirmButton = {
                    TextButton(onClick = {
                        ingredientes.removeAll { it.nombre.equals(ingredienteDuplicado!!.nombre, ignoreCase = true) }
                        ingredientes.add(ingredienteDuplicado!!)
                        nombreIngrediente = ""
                        cantidadIngrediente = ""
                        showReplaceDialog = false
                        Toast.makeText(context, "Ingrediente actualizado", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Reemplazar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReplaceDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}