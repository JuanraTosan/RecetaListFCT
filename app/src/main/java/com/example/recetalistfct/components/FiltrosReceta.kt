package com.example.recetalistfct.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recetalistfct.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltrosReceta(
    navController: NavController,
    onApplyFilter: (String, String, Int) -> Unit
) {
    val tiposComida = listOf(
        stringResource(R.string.breakfast),
        stringResource(R.string.lunch),
        stringResource(R.string.dinner),
        stringResource(R.string.dessert)
    )
    val nivelesDificultad = listOf(
        stringResource(R.string.easy),
        stringResource(R.string.medium),
        stringResource(R.string.hard)
    )

    var tipoSeleccionado by remember { mutableStateOf("") }
    var dificultadSeleccionada by remember { mutableStateOf("") }
    var maxTiempo by remember { mutableStateOf("") }
    var tiempoInt by remember { mutableStateOf<Int>(0) }

    var expandedTipo by remember { mutableStateOf(false) }
    var expandedDificultad by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Selector de tipo de comida
        ExposedDropdownMenuBox(
            expanded = expandedTipo,
            onExpandedChange = { expandedTipo = !expandedTipo },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = tipoSeleccionado,
                onValueChange = {},
                label = { Text(stringResource(R.string.type_of_meal)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                readOnly = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
            ExposedDropdownMenu(
                expanded = expandedTipo,
                onDismissRequest = { expandedTipo = false }
            ) {
                tiposComida.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            tipoSeleccionado = item
                            expandedTipo = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Selector de dificultad
        ExposedDropdownMenuBox(
            expanded = expandedDificultad,
            onExpandedChange = { expandedDificultad = !expandedDificultad },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = dificultadSeleccionada,
                onValueChange = {},
                label = {Text(stringResource(R.string.difficulty_level)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDificultad) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                readOnly = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
            ExposedDropdownMenu(
                expanded = expandedDificultad,
                onDismissRequest = { expandedDificultad = false }
            ) {
                nivelesDificultad.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            dificultadSeleccionada = item
                            expandedDificultad = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de tiempo máximo
        OutlinedTextField(
            value = maxTiempo,
            onValueChange = {
                if (it.isEmpty() || it.toIntOrNull() != null) {
                    maxTiempo = it
                    tiempoInt = it.toIntOrNull() ?: 0
                }
            },
            label = { Text(stringResource(R.string.preparation_time)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón Limpiar filtros
            TextButton(
                onClick = {
                    tipoSeleccionado = ""
                    dificultadSeleccionada = ""
                    maxTiempo = ""
                    tiempoInt = 0
                },
                enabled = tipoSeleccionado.isNotBlank() || dificultadSeleccionada.isNotBlank() || tiempoInt > 0 || maxTiempo.isNotBlank()
            ) {
                Text(text = stringResource(R.string.clear_filters))
            }

            // Botón Aplicar filtros
            Button(
                onClick = {
                    onApplyFilter(
                             tipoSeleccionado,
                             dificultadSeleccionada,
                             tiempoInt
                    )
                },
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(stringResource(R.string.apply_filters))
            }
        }
    }
}