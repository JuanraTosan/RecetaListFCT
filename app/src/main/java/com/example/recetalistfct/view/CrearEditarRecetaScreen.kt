// view/screens/CrearEditarRecetaScreen.kt
package com.example.recetalistfct.view

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.recetalistfct.controller.IngredienteController
import com.example.recetalistfct.controller.RecetaController
import com.example.recetalistfct.controller.RecetaController.obtenerRecetaPorId
import com.example.recetalistfct.model.Ingrediente
import com.example.recetalistfct.model.Receta
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearEditarRecetaScreen(
    navController: NavController,
    recetaId: String? = null
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: return

    var nombre by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var imagenUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var fotosGaleria by rememberSaveable { mutableStateOf<List<Uri>>(emptyList()) } // Lista para fotos extra
    var isUploading by rememberSaveable { mutableStateOf(false) }

    var tipoComida by rememberSaveable { mutableStateOf("") }
    var dificultad by rememberSaveable { mutableStateOf("") }
    var tiempoPreparacion by rememberSaveable { mutableStateOf("") }

    var ingredientes by rememberSaveable { mutableStateOf(mutableListOf<Ingrediente>()) }
    var nombreIngrediente by rememberSaveable { mutableStateOf("") }
    var cantidadIngrediente by rememberSaveable { mutableStateOf("") }

    var showReplaceDialog by rememberSaveable { mutableStateOf(false) }
    var ingredienteDuplicado by rememberSaveable { mutableStateOf<Ingrediente?>(null) }

    var expandedTipo by remember { mutableStateOf(false) }
    var expandedDificultad by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imagenUri = uri }

    // Launcher para seleccionar múltiples imágenes
    val multipleImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris -> fotosGaleria = uris }// Actualizamos la lista con las fotos seleccionadas

    // Cargar datos si estamos editando
    LaunchedEffect(recetaId) {
        if (recetaId != null) {
            obtenerRecetaPorId(recetaId) { receta ->
                if (receta != null) {
                    Log.d("CrearRecetaScreen", "Receta encontrada: ${receta.nombre}")
                    nombre = receta.nombre
                    descripcion = receta.descripcion
                    fotosGaleria = emptyList() // Esto podría sobreescribir imágenes nuevas
                    tipoComida = receta.tipoComida
                    dificultad = receta.dificultad
                    tiempoPreparacion = receta.tiempoPreparacionMin.toString()
                    ingredientes.clear()
                    ingredientes.addAll(receta.ingredientes)
                } else {
                    Log.w("CrearRecetaScreen", "No se encontró la receta con ID: $recetaId")
                }
            }
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
            .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título dinámico
            Text(
                text = if (recetaId == null) "Crear Nueva Receta" else "Edición de receta",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Spacer(Modifier.height(16.dp))

            //Imagen principal receta:
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

            // Tipo de comida
            ExposedDropdownMenuBox(
                expanded = expandedTipo,
                onExpandedChange = {expandedTipo = !expandedTipo},
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = tipoComida,
                    onValueChange = {},
                    label = { Text("Tipo de comida") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                    readOnly = true,
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandedTipo, onDismissRequest = {expandedTipo = false}) {
                    listOf("Desayuno", "Comida", "Cena", "Postres").forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                tipoComida = item
                                expandedTipo = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Dificultad
            ExposedDropdownMenuBox(
                expanded = expandedDificultad,
                onExpandedChange = {expandedDificultad = !expandedDificultad},
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = dificultad,
                    onValueChange = {},
                    label = { Text("Nivel de dificultad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                    readOnly = true,
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(expanded = expandedDificultad, onDismissRequest = {expandedDificultad = false}) {
                    listOf("Fácil", "Medio", "Difícil").forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                dificultad = item
                                expandedDificultad = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tiempo de preparación
            OutlinedTextField(
                value = tiempoPreparacion,
                onValueChange = { tiempoPreparacion = it },
                label = { Text("Tiempo de preparación (min)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))


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
                                cantidad = cantidadIngrediente,
                                recetaIds = listOfNotNull(recetaId)
                            )
                            ingredientes.add(nuevoIngrediente)
                            nombreIngrediente = ""
                            cantidadIngrediente = ""
                        } else {
                            ingredienteDuplicado = Ingrediente(
                                id = UUID.randomUUID().toString(),
                                nombre = nombreIngrediente,
                                cantidad = cantidadIngrediente,
                                recetaIds = listOfNotNull(recetaId)
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

            // Mostrar fotos de la galería como un carrusel horizontal
            Spacer(Modifier.height(16.dp))
            Text("Galería de Fotos", style = MaterialTheme.typography.titleMedium)

            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                fotosGaleria.forEach { uri ->
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                            .clickable { /* Acción cuando se hace clic en la imagen, si deseas verlo en detalle o eliminarlo */ }
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "Imagen galería",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Botón para seleccionar más fotos
            Button(onClick = { multipleImagesLauncher.launch("image/*") }) {
                Text("Añadir fotos a la galería")
            }

            Spacer(Modifier.height(16.dp))


            Button(
                onClick = {
                    if (nombre.isNotBlank() && descripcion.isNotBlank() && imagenUri != null) {
                        isUploading = true
                        RecetaController.subirImagenReceta(imagenUri!!, uid) { fotoUrl ->
                            RecetaController.subirMultiplesImagenesReceta(
                                fotosGaleria,
                                uid
                            ) { galeriaUrls ->
                                if (fotoUrl != null) {


                                    if (recetaId != null) {
                                        val ingredientesConReceta = ingredientes.map { ingrediente ->
                                            if (recetaId in ingrediente.recetaIds) {
                                                ingrediente
                                            } else {
                                                ingrediente.copy(recetaIds = ingrediente.recetaIds + recetaId)
                                            }
                                        }

                                        // Actualizar en base de datos
                                        ingredientesConReceta.forEach { ingredienteActualizado ->
                                            IngredienteController.guardarIngrediente(
                                                ingredienteActualizado
                                            ) {}
                                        }
                                    }

                                    val receta = Receta(
                                        id = recetaId ?: UUID.randomUUID().toString(),
                                        nombre = nombre,
                                        descripcion = descripcion,
                                        fotoReceta = fotoUrl,
                                        usuarioId = uid,
                                        ingredientes = ingredientes.toList(),
                                        fechaCreacion = System.currentTimeMillis(),
                                        fotosGaleriaReceta = galeriaUrls,
                                        tipoComida = tipoComida,
                                        dificultad = dificultad,
                                        tiempoPreparacionMin = tiempoPreparacion.toIntOrNull() ?: 0
                                    )
                                    RecetaController.guardarReceta(receta) { success ->
                                        isUploading = false
                                        if (success) {
                                            Toast.makeText(
                                                context,
                                                "Receta guardada!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.popBackStack() // Vuelve atrás
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Error al guardar receta",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                } else {
                                    isUploading = false
                                    Toast.makeText(
                                        context,
                                        "Error al subir imagen",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isUploading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isUploading) "Guardando..." else if (recetaId == null) "Guardar Receta" else "Actualizar Receta")
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