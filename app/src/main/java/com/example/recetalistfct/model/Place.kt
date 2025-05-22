package com.example.recetalistfct.model

/**
 * Representa un lugar o punto de interés.
 *
 * Esta clase contiene información sobre un lugar específico, como:
 * - Nombre y descripción del lugar.
 * - Coordenadas geográficas (latitud y longitud).
 * - Tipo de lugar (ej. restaurante, supermercado).
 * - Horario de apertura y cierre.
 * - Valoración media del lugar.
 *
 * Se utiliza principalmente en pantallas que muestran mapas y lugares cercanos.
 */
data class Place(
    var name: String = "",
    var type: String = "",
    var descripcion: String = "",
    var lat: Double = 0.0,
    var lon: Double = 0.0,
    var rating: Double = 0.0,
    var horaApertura: String = "",
    var horaCierre: String = ""
)