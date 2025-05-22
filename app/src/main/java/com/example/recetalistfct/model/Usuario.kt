package com.example.recetalistfct.model

/**
 * Representa los datos básicos de un usuario en la aplicación.
 *
 * Esta clase contiene información del perfil del usuario, como:
 * - Nombre de usuario.
 * - Correo electrónico.
 * - Identificador único (UID) de Firebase Authentication.
 * - Información personal opcional (fecha de nacimiento, género, teléfono).
 * - URL de la foto de perfil almacenada en Firebase Storage.
 */
data class Usuario(
    val username: String = "",
    val email: String = "",
    val uid: String = "",
    val fechaNacimiento: String = "",
    val genero: String = "",
    val telefono: String = "",
    val fotoPerfil: String = ""
)