package com.example.recetalistfct.model


/**
 * Representa una receta creada por un usuario.
 *
 * Esta clase contiene información sobre:
 * - Identificador único de la receta.
 * - Nombre y descripción de la receta.
 * - Imagen principal y galería de fotos.
 * - Usuario propietario de la receta.
 * - Lista de ingredientes necesarios.
 * - Información para búsqueda avanzada (tipo de comida, dificultad, tiempo de preparación).
 */
data class Receta(
    var id: String = "",
    val nombre: String = "",
    val descripcion : String = "",
    val fotoReceta : String = "",
    var usuarioId : String = "",
    val ingredientes: List<Ingrediente> = emptyList(),
    val fechaCreacion: Long = System.currentTimeMillis(),
    val fotosGaleriaReceta: List<String> = emptyList(),

    //Campos para búsqueda avanzada:
    val tipoComida :String = "", // "desayuno", "comida", "cena"
    val dificultad: String = "", // "facil", "medio", "dificil"
    val tiempoPreparacionMin: Int = 0 //En minutos
)