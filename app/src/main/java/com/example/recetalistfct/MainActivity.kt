package com.example.recetalistfct

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recetalistfct.session.UserSessionManager
import com.example.recetalistfct.ui.theme.RecetaListFCTTheme
import com.example.recetalistfct.utils.changeLanguage
import com.example.recetalistfct.utils.getSavedLanguage
import com.example.recetalistfct.utils.getSavedThemePreference
import com.example.recetalistfct.view.CarritoScreen
import com.example.recetalistfct.view.CrearEditarRecetaScreen
import com.example.recetalistfct.view.DetalleRecetaScreen
import com.example.recetalistfct.view.HomeScreen
import com.example.recetalistfct.view.LoginScreen
import com.example.recetalistfct.view.MapScreen
import com.example.recetalistfct.view.MisRecetasScreen
import com.example.recetalistfct.view.PerfilScreen
import com.example.recetalistfct.view.RegistroScreen
import com.google.firebase.FirebaseApp

/**
 * Actividad principal de la aplicación.
 *
 * Inicializa Firebase, aplica configuraciones iniciales (idioma, tema oscuro),
 * y establece la navegación entre pantallas usando Jetpack Compose y Jetpack Navigation
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebaseApp = FirebaseApp.initializeApp(this)
        if (firebaseApp != null) {
            Log.d("Firebase", "Firebase está inicializado correctamente.")
        } else {
            Log.e("Firebase", "Firebase NO está inicializado.")
        }

        //Aplicar idioma guardado
        val savedLang = getSavedLanguage(this)
        changeLanguage(this, savedLang)

        //Recuperar preferencia de tema oscuro
        val savedDarkMode = getSavedThemePreference(this)

        //Inicializar sesión del usuario
        UserSessionManager.init(this)

        //Habilitar modo Edge-to-Edge (Sin barras negras en dispositivos modernos)
        enableEdgeToEdge()

        //Configurar contenido de la UI con Compose
        setContent {
            val darkThemeState = remember { mutableStateOf(savedDarkMode) }

            //Aplicar tema personalizado con soporte para modo oscuro
            RecetaListFCTTheme(useDarkTheme = darkThemeState.value) {
                val navController = rememberNavController()

                MainScreen(navController = navController)
            }
        }
    }
}


/**
 * Punto de entrada principal de la aplicación.
 *
 * Define las rutas de navegación entre pantallas.
 *
 * @param navController Controlador de navegación para gestionar las transiciones entre pantallas.
 */
@Composable
fun MainScreen(
    navController: NavHostController
){
    //Pantalla inicial según si hay un usuario autenticado
    val startDestination = if (UserSessionManager.isUserLoggedIn()) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination){
        composable("login") {
            LoginScreen(
                navController = navController
            )
        }
        composable("register"){
            RegistroScreen(
                navController = navController
            )
        }
        composable("home"){
            HomeScreen(
                navController = navController
            )
        }
        composable("perfil"){
            PerfilScreen(
                navController = navController
            )
        }
        composable("recetas") {
            MisRecetasScreen(
                navController = navController
            )
        }
        composable("crearRecetas/{recetaId}") { backStackEntry ->
            val recetaId = backStackEntry.arguments!!.getString("recetaId")!!
            CrearEditarRecetaScreen(
                navController = navController,
                recetaId = recetaId
            )
        }
        composable(route = "crearRecetas") {
            CrearEditarRecetaScreen(navController = navController)
        }
        composable("map") {
            MapScreen(
                navController = navController
            )
        }

        composable("carrito") {
            CarritoScreen(navController = navController)
        }

        composable("detalleRecetas/{recetaId}") { backStackEntry ->
            val recetaId = backStackEntry.arguments!!.getString("recetaId")!!
            DetalleRecetaScreen(
                navController = navController,
                recetaId = recetaId
            )
        }
    }
}