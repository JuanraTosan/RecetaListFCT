package com.example.recetalistfct.model

data class Receta(
    val id: String = "",
    val nombre: String = "",
    val descripcion : String = "",
    val fotoReceta : String = "",
    val usuarioId : String = "",
    val ingredientes: List<Ingrediente> = emptyList()
)
