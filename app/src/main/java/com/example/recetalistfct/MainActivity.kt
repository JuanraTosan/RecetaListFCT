package com.example.recetalistfct

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recetalistfct.ui.theme.RecetaListFCTTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebaseApp = FirebaseApp.initializeApp(this)
        if (firebaseApp != null) {
            Log.d("Firebase", "Firebase está inicializado correctamente.")
        } else {
            Log.e("Firebase", "Firebase NO está inicializado.")
        }
        /*
        // 🔸 Prueba de conexión con Firestore
        val db = FirebaseFirestore.getInstance()
        val receta = hashMapOf(
            "nombre" to "Tortilla",
            "ingredientes" to "Huevos y papas"
        )
        db.collection("recetas")
            .add(receta)
            .addOnSuccessListener { documentReference ->
                println("Documento añadido con ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Error al añadir documento: $e")
            }
         */
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
        composable("home/{usuarioId}"){
            HomeScreen(
                navController = navController
            )
        }
    }
}