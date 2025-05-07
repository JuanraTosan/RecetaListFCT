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
import com.example.recetalistfct.session.userSessionManager
import com.example.recetalistfct.ui.theme.RecetaListFCTTheme
import com.example.recetalistfct.utils.changeLanguage
import com.example.recetalistfct.utils.getSavedLanguage
import com.example.recetalistfct.utils.getSavedThemePreference
import com.example.recetalistfct.view.CarritoScreen
import com.example.recetalistfct.view.CrearRecetaScreen
import com.example.recetalistfct.view.DetalleRecetaScreen
import com.example.recetalistfct.view.HomeScreen
import com.example.recetalistfct.view.LoginScreen
import com.example.recetalistfct.view.MapScreen
import com.example.recetalistfct.view.MisRecetasScreen
import com.example.recetalistfct.view.PerfilScreen
import com.example.recetalistfct.view.RegistroScreen
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebaseApp = FirebaseApp.initializeApp(this)
        if (firebaseApp != null) {
            Log.d("Firebase", "Firebase está inicializado correctamente.")
        } else {
            Log.e("Firebase", "Firebase NO está inicializado.")
        }

        //Guardar el idioma seleccionado
        val savedLang = getSavedLanguage(this)
        changeLanguage(this, savedLang)

        //Aqui se recupera el modo oscuro
        val savedDarkMode = getSavedThemePreference(this)

        userSessionManager.init(this)

        enableEdgeToEdge()

        setContent {
            val darkThemeState = remember { mutableStateOf(savedDarkMode) }
            RecetaListFCTTheme(useDarkTheme = darkThemeState.value) {
                val navController = rememberNavController()

                MainScreen(navController = navController)
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController
){
    val startDestination = if (userSessionManager.isUserLoggedIn()) "home" else "login"

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
        composable("crearRecetas") {
            CrearRecetaScreen(
                navController = navController
            )
        }
        composable("map") {
            MapScreen(
                navController = navController
            )
        }
        composable("carrito") {
            CarritoScreen(
                navController = navController
            )
        }
        composable("detalleRecetas") {
            DetalleRecetaScreen(
                navController = navController
            )
        }
    }
}