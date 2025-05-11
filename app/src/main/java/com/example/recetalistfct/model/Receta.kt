package com.example.recetalistfct.model

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