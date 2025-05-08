package com.example.recetalistfct.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import java.util.Calendar
import androidx.compose.material3.*
import androidx.compose.runtime.*


@Composable
fun FechaNacimientoField(fechaNacimiento: String, onFechaSeleccionada: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Mostrar DatePicker al hacer clic
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // Formatear la fecha como dd/MM/yyyy
                val fecha = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                onFechaSeleccionada(fecha)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    OutlinedTextField(
        value = fechaNacimiento,
        onValueChange = {},
        label = { Text("Fecha de nacimiento") },
        readOnly = true,
        modifier = Modifier,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Seleccionar fecha",
                modifier = Modifier
                    .semantics { contentDescription = "Icono calendario" }
                    .clickable { datePickerDialog.show() }
            )
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneroDropdownField(
    generoSeleccionado: String,
    onGeneroSeleccionado: (String) -> Unit
) {
    val opciones = listOf("Masculino", "Femenino", "Otros", "Prefiero no decirlo")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = generoSeleccionado,
            onValueChange = {},
            readOnly = true,
            label = { Text("Género") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .clickable { expanded = true }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onGeneroSeleccionado(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}
