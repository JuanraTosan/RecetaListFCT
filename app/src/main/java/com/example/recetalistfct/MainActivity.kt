package com.example.recetalistfct

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recetalistfct.session.userSessionManager
import com.example.recetalistfct.ui.theme.RecetaListFCTTheme
import com.example.recetalistfct.view.CrearRecetaScreen
import com.example.recetalistfct.view.HomeScreen
import com.example.recetalistfct.view.LoginScreen
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

        enableEdgeToEdge()

        setContent {
            RecetaListFCTTheme {
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
    val userId = userSessionManager.currentUserId
    NavHost(navController = navController, startDestination = "login"){
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
    }
}