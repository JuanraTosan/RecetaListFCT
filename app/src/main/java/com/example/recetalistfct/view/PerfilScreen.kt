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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
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
import com.example.recetalistfct.components.BottomBar
import com.example.recetalistfct.components.FechaNacimientoField
import com.example.recetalistfct.components.GeneroDropdownField
import com.example.recetalistfct.controller.UsuarioController.actualizarPerfil
import com.example.recetalistfct.controller.UsuarioController.obtenerUsuario
import com.example.recetalistfct.controller.UsuarioController.subirFotoPerfil
import com.example.recetalistfct.model.Usuario
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PerfilScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: return

    var usuario by remember { mutableStateOf<Usuario?>(null) }

    var username by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var fotoPerfil by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            imagenUri = uri
        }
    )

    // Cargar datos del usuario al iniciar
    LaunchedEffect(uid) {
        obtenerUsuario(uid) { fetchedUser ->
            fetchedUser?.let {
                usuario = it
                username = it.username
                telefono = it.telefono
                fechaNacimiento = it.fechaNacimiento
                genero = it.genero
                fotoPerfil = it.fotoPerfil
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomBar(selectedItem = "perfil") { navController.navigate(it) }
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Configuración de Perfil", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(16.dp))

            Box(modifier = Modifier.size(120.dp)) {
                val imageModifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .clickable { launcher.launch("image/*") }
                    .background(Color.Gray)

                if (imagenUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imagenUri),
                        contentDescription = "Nueva Foto de perfil",
                        modifier = imageModifier
                    )
                }else if (fotoPerfil.isNotBlank()){
                    Image(
                        painter = rememberAsyncImagePainter(fotoPerfil),
                        contentDescription = "Foto actual de perfil",
                        modifier = imageModifier
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = imageModifier)
                }

                IconButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Cambiar foto")
                }
            }

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de usuario") })

            FechaNacimientoField(fechaNacimiento){
                fechaNacimiento = it
            }

            GeneroDropdownField(genero) {
                genero = it
            }

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") })

            Spacer(Modifier.height(16.dp))

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
                    actualizarUsuario(fotoPerfil) // Si no se seleccionó nueva foto, usar la URL ya guardada
                }
                          },
        modifier = Modifier.fillMaxWidth()
        ) {
        Text("Guardar cambios")
    }
    }
}
}