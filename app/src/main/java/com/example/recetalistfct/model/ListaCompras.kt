package com.example.recetalistfct.model

/**
 * Representa una lista de la compra asociada a un usuario.
 *
 * Esta clase contiene información sobre:
 * - Un identificador único de la lista.
 * - El nombre o título de la lista.
 * - Los ingredientes que incluye.
 * - Las categorías asociadas (ej. "Desayuno", "Cena").
 * - La fecha de creación.
 * - El ID del usuario al que pertenece la lista.
 *
 * Se utiliza principalmente en el carrito de compras para almacenar y organizar ingredientes.
 */
data class ListaCompras(
    val id: String = "",
    val nombre: String = "",
    val ingredientes: List<Ingrediente> = emptyList(),
    val categorias: List<String> = emptyList(),
    val fechaCreacion: Long = System.currentTimeMillis(),
    val usuarioId: String = ""
)
