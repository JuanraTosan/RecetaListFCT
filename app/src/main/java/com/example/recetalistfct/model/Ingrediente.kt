package com.example.recetalistfct.model

data class Ingrediente(
    val id: String = "",
    val nombre: String = "",
    val cantidad: String = "",
    val recetaIds: List<String> = emptyList(),
    var comprado: Boolean = false
)
