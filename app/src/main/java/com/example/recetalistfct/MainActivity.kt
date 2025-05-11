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
//import com.google.android.libraries.places.api.Places
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

        UserSessionManager.init(this)

/*
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyC89hxw56weyKc77KPDYU_05L0LBr_cy5w")
        }
*/


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
            CarritoScreen(
                navController = navController
            )
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