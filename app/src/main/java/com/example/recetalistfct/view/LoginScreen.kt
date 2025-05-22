package com.example.recetalistfct.view

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recetalistfct.R
import com.example.recetalistfct.controller.UsuarioController
import com.example.recetalistfct.session.UserSessionManager

/**
 * Pantalla de inicio de sesión.
 *
 * Permite al usuario:
 * - Introducir su correo electrónico y contraseña
 * - Iniciar sesión o navegar a la pantalla de registro si no tiene cuenta.
 * - Mantener los datos introducidos frente a rotaciones de pantalla gracias a 'rememberSaveable'.
 *
 * @param navController Controlador de navegación para cambiar a entre pantallas.
 */
@Composable
fun LoginScreen(
    navController: NavController
) {
    //Campos de texto para email y contraseña
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    //Estado para mostrar mensajes de error
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    //Detecta si el dispositivo está en modo horizontal(landscape)
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current


    /**
     * Capa principal de la pantalla con:
     * - Fondo degradado.
     * - Detección de toques para limpiar el foco del teclado
     * - Imagen de logo semi-transparente centrada.
     */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding() // Evita que el teclado tape la UI
            .pointerInput(Unit){
                detectTapGestures ( onTap = {
                    focusManager.clearFocus()
                })
            }
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                    )
                )
            )
    ) {
        // Logo de la aplicación con tamaño adaptativo según orientación
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(if (isLandscape) 200.dp else 300.dp)
                .align(Alignment.Center)
                .padding(top = if (isLandscape) 32.dp else 64.dp)
                .alpha(0.25f),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            //Titulo de la pantalla
            Text(
                text = stringResource(R.string.login),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de texto para el email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email)) },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Gray
                ),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de texto para la contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Gray
                ),
            )

            Spacer(modifier = Modifier.height(8.dp))

            //Mostrar mensaje de error si lo hay
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            //Botón de iniciar sesión
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()){
                        UsuarioController.loginUsuario(
                            email = email,
                            password = password,
                            context = context,
                            onSuccess = {usuario ->
                                UserSessionManager.updateUser(usuario.uid)
                                navController.navigate("home"){
                                    popUpTo("login"){inclusive = true}
                                }
                            },
                            onError = {msg -> errorMessage = msg}
                        )
                    }else{
                        errorMessage = "Rellena todos los campos"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB0BEC5),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(R.string.login))
            }

            Spacer(modifier = Modifier.height(8.dp))

            //Botón para  navegar a la pantalla de registro
            TextButton(onClick = { navController.navigate("register") }) {
                Text(stringResource(R.string.dont_have_account_register))
            }
        }
    }
}