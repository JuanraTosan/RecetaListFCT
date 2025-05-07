package com.example.recetalistfct.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recetalistfct.components.BottomBar

@Composable
fun MapScreen(navController: NavController) {

    Scaffold(
        bottomBar = {
            BottomBar(selectedItem = "map") { navController.navigate(it) }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("EN PROCESO...", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}