package com.example.recetalistfct.components

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.recetalistfct.R
import androidx.compose.ui.res.stringResource
import com.example.recetalistfct.ui.theme.LocalDarkTheme
import com.example.recetalistfct.utils.changeLanguage
import com.example.recetalistfct.utils.saveLanguagePreference
import com.example.recetalistfct.utils.saveThemePreference
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import com.example.recetalistfct.session.userSessionManager


@Composable
fun FastSettings(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    context: Context,
    activity: Activity
) {
    val languages = listOf("es", "en", "fr") // Lista de códigos de idioma
    val languageNames = listOf("Español", "Inglés", "Francés") // Nombres de idiomas

    var showLanguageMenu by remember { mutableStateOf(false) }
    val darkThemeState = LocalDarkTheme.current


    Box(modifier = modifier) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.logout)) },
                onClick = {
                    userSessionManager.clearSession()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                    onDismissRequest()
                }
            )

            DropdownMenuItem(
                text = {
                    Text(if (darkThemeState.value) "Tema claro" else "Tema oscuro")
                },
                trailingIcon = {
                    Switch(
                        checked = darkThemeState.value,
                        onCheckedChange = {
                            darkThemeState.value = it
                            saveThemePreference(context, it)
                        },
                        colors = SwitchDefaults.colors()
                    )
                },
                onClick = {
                    val newValue = !darkThemeState.value
                    darkThemeState.value = newValue
                    saveThemePreference(context, newValue)
                }
            )

            // Botón principal para cambiar idioma
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.change_language)) },
                onClick = {
                    showLanguageMenu = !showLanguageMenu
                }
            )

            // Submenú con idiomas
            if (showLanguageMenu) {
                languages.forEachIndexed { index, langCode ->
                    DropdownMenuItem(
                        text = { Text(languageNames[index]) },
                        onClick = {
                            changeLanguage(context, langCode)
                            saveLanguagePreference(context, langCode)
                            showLanguageMenu = false
                            onDismissRequest()
                            activity.recreate()
                        }
                    )
                }
            }
        }
    }
}