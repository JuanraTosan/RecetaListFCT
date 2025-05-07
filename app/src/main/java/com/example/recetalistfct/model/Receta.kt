package com.example.recetalistfct.model

data class Receta(
    var id: String = "",
    val nombre: String = "",
    val descripcion : String = "",
    val fotoReceta : String = "",
    var usuarioId : String = "",
    val ingredientes: List<Ingrediente> = emptyList(),
    val fechaCreacion: Long = System.currentTimeMillis()
)
