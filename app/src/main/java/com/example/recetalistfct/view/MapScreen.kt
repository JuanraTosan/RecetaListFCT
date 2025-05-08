package com.example.recetalistfct.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.example.recetalistfct.R
import com.example.recetalistfct.components.BottomBar
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun MapScreen(navController: NavController) {
    var visible by remember { mutableStateOf(false) }
    var vibrationOffset by remember { mutableStateOf(0f) }

    // Animación de aparición
    LaunchedEffect(Unit) {
        delay(300)
        visible = true
    }

    // Vibración horizontal ligera
    LaunchedEffect(Unit) {
        var time = 0f
        while (true) {
            vibrationOffset = (sin(time) * 5).toFloat()
            time += 0.02f
            delay(16)
        }
    }

    Scaffold(
        bottomBar = {
            BottomBar(selectedItem = "map") { navController.navigate(it) }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Mostrar GIF
                    AsyncImage(
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(R.drawable.nuggetspinning)
                            .decoderFactory(GifDecoder.Factory())
                            .build(),
                        contentDescription = "GIF Nugget",
                        modifier = Modifier.size(200.dp)
                    )

                    // Texto con vibración
                    Text(
                        text = "ESTAMOS TRABAJANDO \n EN ELLO...",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 25.sp),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.offset(x = vibrationOffset.dp)
                    )
                }
            }
        }
    }
}