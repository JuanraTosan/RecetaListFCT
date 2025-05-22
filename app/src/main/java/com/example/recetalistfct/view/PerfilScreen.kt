// ui/screens/PerfilScreen.kt
package com.example.recetalistfct.view

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.recetalistfct.R
import com.example.recetalistfct.components.BottomBar
import com.example.recetalistfct.components.FechaNacimientoField
import com.example.recetalistfct.components.GeneroDropdownField
import com.example.recetalistfct.controller.UsuarioController.actualizarPerfil
import com.example.recetalistfct.controller.UsuarioController.obtenerUsuario
import com.example.recetalistfct.controller.UsuarioController.subirFotoPerfil
import com.example.recetalistfct.model.Usuario
import com.google.firebase.auth.FirebaseAuth

/**
 * Pantalla de Configuracion de perfil del usuario.
 *
 * Permite al usuario:
 * -Ver su información actual (Nombre, fecha de nacimiento, genero, teléfono).
 * -Cambiar la foto de perfil.
 * -Editar datos personales y guardar los cambios en Firebase.
 *
 * @param navController Controlador de navegación para cambiar entre pantallas.
 */
@Composable
fun PerfilScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: return

    //Datos completos del usuario obtenidos desde Firebase
    var usuario by remember { mutableStateOf<Usuario?>(null) }

    var username by rememberSaveable { mutableStateOf("") }
    var fechaNacimiento by rememberSaveable { mutableStateOf("") }
    var genero by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var imagenUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var fotoPerfil by rememberSaveable { mutableStateOf("") }

    //Indicador para evitar recargar datos múltiples veces
    val usuarioCargado = rememberSaveable { mutableStateOf(false) }

    //Launcher para seleccionar una foto de perfil desde la galería
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            imagenUri = uri
        }
    )

    /**
     * Carga los datos del usuario desde Firebase cuando se entra a esta pantalla
     * Solo se ejecuta una vevz gracias a 'usuarioCargado'.
     */
    LaunchedEffect(uid) {
        if (!usuarioCargado.value) {
            obtenerUsuario(uid) { fetchedUser ->
                fetchedUser?.let {
                    usuario = it
                    username = it.username
                    telefono = it.telefono
                    fechaNacimiento = it.fechaNacimiento
                    genero = it.genero
                    fotoPerfil = it.fotoPerfil
                    usuarioCargado.value = true
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomBar(selectedItem = "perfil") { navController.navigate(it) }
        }
    ) { padding ->

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {

            //SECCION ENCABEZADO (TITULO Y FOTO DE PERFIL)
            item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
                    .padding(vertical = 24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    //Titulo de la pantalla
                    Text(
                        stringResource(R.string.my_profile),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Contenedor para la foto de perfil
                    Box {
                        val imageModifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .clickable { launcher.launch("image/*") }
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))

                        if (imagenUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(imagenUri),
                                contentDescription = "Nueva Foto de perfil",
                                modifier = imageModifier
                            )
                        } else if (fotoPerfil.isNotBlank()) {
                            Image(
                                painter = rememberAsyncImagePainter(fotoPerfil),
                                contentDescription = "Foto actual de perfil",
                                modifier = imageModifier
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Foto de perfil",
                                modifier = imageModifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        //Botón para editar la foto de perfil
                        IconButton(
                            onClick = { launcher.launch("image/*") },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .size(30.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Cambiar foto",
                                tint = Color.White
                            )
                        }
                    }
                }
            }



            Spacer(modifier = Modifier.height(30.dp))
            }

            //SECCION PRINCIPAL: Formulario de edición de perfil
            item {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    //Campo para el nombre de usuario
                    OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(stringResource(R.string.username)) },
                    modifier = Modifier.fillMaxWidth(0.8f)
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    //Campo para la fecha de nacimiento (componente reutilizable)
                    FechaNacimientoField(fechaNacimiento) {
                    fechaNacimiento = it
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    //Campo para seleccionar genero (componente reutilizable)
                    GeneroDropdownField(genero) {
                    genero = it
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    //Campo para el número de teléfono
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text(stringResource(R.string.phone_number)) },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    //Botón para guardar los cambios
                    Button(
                        onClick = {
                            val actualizarUsuario: (String) -> Unit = { urlFinal ->
                                val nuevoUsuario = Usuario(
                                    uid = uid,
                                    email = usuario?.email ?: "",
                                    username = username,
                                    telefono = telefono,
                                    fechaNacimiento = fechaNacimiento,
                                    genero = genero,
                                    fotoPerfil = urlFinal
                                )
                                actualizarPerfil(nuevoUsuario)
                                Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                            }

                            if (imagenUri != null) {
                                subirFotoPerfil(imagenUri!!, uid) { urlSubida ->
                                    if (urlSubida != null) {
                                        actualizarUsuario(urlSubida)
                                        fotoPerfil = urlSubida
                                    } else {
                                        Toast.makeText(context, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                actualizarUsuario(fotoPerfil)
                            }
                                  },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(stringResource(R.string.save_changes))
                    }
                }
            }
        }
    }
}