package com.example.recetalistfct.model

data class ListaCompras(
    val id: String = "",
    val nombre: String = "",
    val ingredientes: List<Ingrediente> = emptyList(),
    val categorias: List<String> = emptyList(),
    val fechaCreacion: Long = System.currentTimeMillis(),
    val usuarioId: String = ""
)
