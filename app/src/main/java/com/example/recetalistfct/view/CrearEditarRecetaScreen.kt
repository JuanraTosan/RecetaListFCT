package com.example.recetalistfct.view

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.recetalistfct.R
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
    var fotoUrl by rememberSaveable { mutableStateOf("") } // Foto principal desde Firebase
    var fotosGaleria by rememberSaveable { mutableStateOf<List<Uri>>(emptyList()) } // Lista para fotos extra
    var fotoUrls by rememberSaveable { mutableStateOf<List<String>>(emptyList()) } // Fotos de galería desde Firebase
    var isUploading by rememberSaveable { mutableStateOf(false) }

    var tipoComida by rememberSaveable { mutableStateOf("") }
    var dificultad by rememberSaveable { mutableStateOf("") }
    var tiempoPreparacion by rememberSaveable { mutableStateOf("") }

    var ingredientes by remember { mutableStateOf(mutableListOf<Ingrediente>()) }

    var nombreIngrediente by rememberSaveable { mutableStateOf("") }
    var cantidadIngrediente by rememberSaveable { mutableStateOf("") }
    var unidadMedida by rememberSaveable { mutableStateOf("") }

    var showReplaceDialog by rememberSaveable { mutableStateOf(false) }
    var ingredienteDuplicado by rememberSaveable { mutableStateOf<Ingrediente?>(null) }

    var expandedTipo by remember { mutableStateOf(false) }
    var expandedDificultad by remember { mutableStateOf(false) }
    var expandedUnidadMedida by remember { mutableStateOf(false) }


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
                    //foto principal
                    fotoUrl = receta.fotoReceta
                    //fotos de galeria
                    fotoUrls = receta.fotosGaleriaReceta

                    //limpiar cualquier seleccion previa de nueva imagen
                    imagenUri = null
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
                stringResource(id = if (recetaId == null) R.string.create_recipe else R.string.edit_recipe),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Spacer(Modifier.height(16.dp))

            //Imagen principal receta:
            Box(
                modifier = Modifier
                    .fillMaxWidth(1f) // Ocupa el 95% del ancho
                    .height(300.dp)
                    .clip(RoundedCornerShape(8.dp)) // Cuadrado con esquinas ligeramente redondeadas
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                when {
                    imagenUri != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(model = imagenUri),
                            contentDescription = "Imagen nueva de receta",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    fotoUrl.isNotBlank() -> {
                        Image(
                            painter = rememberAsyncImagePainter(model = fotoUrl),
                            contentDescription = "Imagen actual de receta",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    else -> {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Seleccionar imagen",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text(stringResource(R.string.recipe_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            // Tipo de comida
            ExposedDropdownMenuBox(
                expanded = expandedTipo,
                onExpandedChange = {expandedTipo = !expandedTipo},
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = tipoComida,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.type_of_meal)) },
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

            Spacer(Modifier.height(10.dp))

            // Dificultad
            ExposedDropdownMenuBox(
                expanded = expandedDificultad,
                onExpandedChange = {expandedDificultad = !expandedDificultad},
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = dificultad,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.difficulty_level)) },
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
                label = { Text(stringResource(R.string.preparation_time)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Ingredientes
            Text(stringResource(R.string.ingredients), style = MaterialTheme.typography.titleMedium)
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Fila 1: nombre del ingrediente y cantidad:
                    OutlinedTextField(
                        value = nombreIngrediente,
                        onValueChange = { nombreIngrediente = it },
                        label = { Text(stringResource(R.string.name)) },
                        modifier = Modifier.weight(0.6f)
                    )

                    // Campo cantidad
                    OutlinedTextField(
                        value = cantidadIngrediente,
                        onValueChange = { cantidadIngrediente = it },
                        label = { Text(stringResource(R.string.amount)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.3f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Fila 2: Unidad de Medida y Botón +
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Selector de unidad de medida
                    ExposedDropdownMenuBox(
                        expanded = expandedUnidadMedida,
                        onExpandedChange = { expandedUnidadMedida = !expandedUnidadMedida },
                        modifier = Modifier.weight(0.7f)
                    ) {
                        OutlinedTextField(
                            value = unidadMedida,
                            onValueChange = {},
                            label = { Text(stringResource(R.string.unit_of_measure)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnidadMedida) },
                            readOnly = true,
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedUnidadMedida,
                            onDismissRequest = { expandedUnidadMedida = false }
                        ) {
                            listOf(
                                stringResource(R.string.grams),
                                stringResource(R.string.kilograms),
                                stringResource(R.string.units),
                                stringResource(R.string.liters),
                                stringResource(R.string.tablespoons)
                            ).forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        unidadMedida = item
                                        expandedUnidadMedida = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botón Añadir Ingrediente
                    Button(
                        onClick = {
                            if (nombreIngrediente.isNotBlank() && cantidadIngrediente.isNotBlank()) {
                                val existe = ingredientes.any {
                                    it.nombre.equals(
                                        nombreIngrediente,
                                        ignoreCase = true
                                    )
                                }

                                if (!existe) {
                                    val nuevoIngrediente = Ingrediente(
                                        id = UUID.randomUUID().toString(),
                                        nombre = nombreIngrediente,
                                        cantidad = cantidadIngrediente,
                                        recetaIds = listOfNotNull(recetaId),
                                        unidadMedida = unidadMedida
                                    )
                                    ingredientes.add(nuevoIngrediente)
                                    nombreIngrediente = ""
                                    cantidadIngrediente = ""
                                    unidadMedida = ""
                                } else {
                                    ingredienteDuplicado = Ingrediente(
                                        id = UUID.randomUUID().toString(),
                                        nombre = nombreIngrediente,
                                        cantidad = cantidadIngrediente,
                                        recetaIds = listOfNotNull(recetaId),
                                        unidadMedida = unidadMedida
                                    )
                                    showReplaceDialog = true
                                }
                            }
                        },
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir ingrediente")
                    }
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
                    Text("• ${ingrediente.nombre} - ${ingrediente.cantidad} (${ingrediente.unidadMedida})", modifier = Modifier.weight(1f))
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
            Text(stringResource(R.string.gallery_pictures), style = MaterialTheme.typography.titleMedium)

            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                // Mostrar fotos previamente subidas
                fotoUrls.forEach { url ->
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(url),
                            contentDescription = "Foto de receta",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Mostrar nuevas fotos seleccionadas
                fotosGaleria.forEach { uri ->
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "Nueva foto de galería",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { multipleImagesLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.add_photos_to_gallery))
            }

            Spacer(Modifier.height(24.dp))


            Button(
                onClick = {
                    if (nombre.isNotBlank() && descripcion.isNotBlank() && imagenUri != null || fotoUrl.isNotBlank()) {
                        isUploading = true

                        // Usar foto actual o subir una nueva
                        if (imagenUri != null) {
                            // Subir nueva imagen y actualizar
                            RecetaController.subirImagenReceta(imagenUri!!, uid) { nuevaUrlFoto ->
                                if (nuevaUrlFoto == null) {
                                    isUploading = false
                                    Toast.makeText(context, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                                    return@subirImagenReceta
                                }

                                // Subir nuevas fotos de galería
                                RecetaController.subirMultiplesImagenesReceta(fotosGaleria, uid) { galeriaUrls ->
                                    val todasLasFotosGaleria = if (recetaId != null) {
                                        // Modo edición: combinar fotos anteriores con nuevas subidas
                                        (fotoUrls + galeriaUrls).distinct()
                                    } else {
                                        // Modo creación: usar solo las nuevas fotos
                                        galeriaUrls
                                    }

                                    // Crear receta con las fotos adecuadas
                                    val receta = Receta(
                                        id = recetaId ?: UUID.randomUUID().toString(),
                                        nombre = nombre,
                                        descripcion = descripcion,
                                        fotoReceta = nuevaUrlFoto,
                                        usuarioId = uid,
                                        ingredientes = ingredientes.toList(),
                                        fechaCreacion = System.currentTimeMillis(),
                                        fotosGaleriaReceta = todasLasFotosGaleria,
                                        tipoComida = tipoComida,
                                        dificultad = dificultad,
                                        tiempoPreparacionMin = tiempoPreparacion.toIntOrNull() ?: 0
                                    )

                                    // Guardar receta
                                    RecetaController.guardarReceta(receta) { success ->
                                        isUploading = false
                                        if (success) {
                                            Toast.makeText(context, "Receta guardada!", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                        } else {
                                            Toast.makeText(context, "Error al guardar receta", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    // Actualizar ingredientes con nuevo ID si estamos editando
                                    if (recetaId != null) {
                                        val ingredientesConReceta = ingredientes.map { ingrediente ->
                                            if (recetaId in ingrediente.recetaIds) {
                                                ingrediente
                                            } else {
                                                ingrediente.copy(recetaIds = ingrediente.recetaIds + recetaId)
                                            }
                                        }

                                        ingredientesConReceta.forEach { ingredienteActualizado ->
                                            IngredienteController.guardarIngrediente(ingredienteActualizado) {}
                                        }
                                    } else {
                                        // Modo creación: guardar ingredientes con nuevo ID y vincular a la nueva receta
                                        val ingredientesConReceta = ingredientes.map { ingrediente ->
                                            ingrediente.copy(id = UUID.randomUUID().toString(), recetaIds = listOf(receta.id))
                                        }

                                        ingredientesConReceta.forEach { ingrediente ->
                                            IngredienteController.guardarIngrediente(ingrediente) {}
                                        }
                                    }
                                }
                            }
                        } else {
                            // Si hay imagenUri, usa esa; si no, usa la fotoUrl actual
                            fotoUrl.takeIf { recetaId != null && it.isNotBlank() }?.let { existingUrl ->

                                // No se ha seleccionado nueva imagen → mantener fotoUrl
                                RecetaController.subirMultiplesImagenesReceta(fotosGaleria, uid) { galeriaUrls ->
                                    val todasLasFotosGaleria = if (recetaId != null) {
                                        (fotoUrls + galeriaUrls).distinct()
                                    } else {
                                        galeriaUrls
                                    }

                                    val receta = Receta(
                                        id = recetaId ?: UUID.randomUUID().toString(),
                                        nombre = nombre,
                                        descripcion = descripcion,
                                        fotoReceta = fotoUrl,
                                        usuarioId = uid,
                                        ingredientes = ingredientes.toList(),
                                        fechaCreacion = System.currentTimeMillis(),
                                        fotosGaleriaReceta = todasLasFotosGaleria,
                                        tipoComida = tipoComida,
                                        dificultad = dificultad,
                                        tiempoPreparacionMin = tiempoPreparacion.toIntOrNull() ?: 0
                                    )

                                    RecetaController.guardarReceta(receta) { success ->
                                        isUploading = false
                                        if (success) {
                                            Toast.makeText(context, "Receta guardada!", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                        } else {
                                            Toast.makeText(context, "Error al guardar receta", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    if (recetaId != null) {
                                        val ingredientesConReceta = ingredientes.map { ingrediente ->
                                            if (recetaId in ingrediente.recetaIds) {
                                                ingrediente
                                            } else {
                                                ingrediente.copy(recetaIds = ingrediente.recetaIds + recetaId)
                                            }
                                        }

                                        ingredientesConReceta.forEach { ingrediente ->
                                            IngredienteController.guardarIngrediente(ingrediente) {}
                                        }
                                    }
                                }
                            } ?: run {
                                isUploading = false
                                Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isUploading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isUploading) stringResource(R.string.uploading) else if (recetaId == null) stringResource(R.string.save_recipe) else stringResource(R.string.update_recipe))
            }
        }

        // Diálogo de reemplazo
        if (showReplaceDialog && ingredienteDuplicado != null) {
            AlertDialog(
                onDismissRequest = { showReplaceDialog = false },
                title = { Text(stringResource(R.string.duplicate_ingredient)) },
                text = { Text(stringResource(R.string.duplicate_ingredient_text)) },
                confirmButton = {
                    TextButton(onClick = {
                        ingredientes.removeAll { it.nombre.equals(ingredienteDuplicado!!.nombre, ignoreCase = true) }
                        ingredientes.add(ingredienteDuplicado!!)
                        nombreIngrediente = ""
                        cantidadIngrediente = ""
                        showReplaceDialog = false
                        Toast.makeText(context, "Ingrediente actualizado", Toast.LENGTH_SHORT).show()
                    }) {
                        Text(stringResource(R.string.replace))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReplaceDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}