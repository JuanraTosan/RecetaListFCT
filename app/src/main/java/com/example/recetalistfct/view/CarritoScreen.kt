package com.example.recetalistfct.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recetalistfct.R
import com.example.recetalistfct.components.BottomBar
import com.example.recetalistfct.controller.CarritoController
import com.example.recetalistfct.controller.CarritoController.cargarListaComprasDeUsuario
import com.example.recetalistfct.controller.CarritoController.eliminarSeleccionados
import com.example.recetalistfct.model.ListaCompras
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CarritoScreen(navController: NavController) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = remember { mutableStateOf(currentUser?.uid) }
    var lista by remember { mutableStateOf<ListaCompras?>(null) }
    var ingredientes by remember { mutableStateOf(CarritoController.ingredientes) }


    // Recarga la lista cuando el usuario cambia
    LaunchedEffect(userId.value) {
        if (userId.value != null) {
            cargarListaComprasDeUsuario(userId.value!!) {
                lista = it
            }
        }
    }


    Scaffold(
        bottomBar = {
            BottomBar(selectedItem = "carrito") { navController.navigate(it) }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.shopping_list), style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(ingredientes, key = { _, ing -> ing.nombre }) { index, ing ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(6.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = ing.comprado,
                                    onCheckedChange = {
                                        CarritoController.toggleComprado(index)
                                        CarritoController.guardarCambios()
                                    },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = "${ing.nombre} - ${ing.cantidad} (${ing.unidadMedida})",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val seleccionadosAntes = ingredientes.count { it.comprado }
                        eliminarSeleccionados()

                        if (seleccionadosAntes > 0) {
                            Toast.makeText(context, "Ingredientes eliminados", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Ningún ingrediente seleccionado", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.remove_selected))
                }
            }
        }
    }
}