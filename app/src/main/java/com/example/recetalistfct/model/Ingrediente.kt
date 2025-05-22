package com.example.recetalistfct.model


/**
 * Representa un ingrediente utilizado en una receta.
 *
 * Esta clase contiene información básica sobre un ingrediente, incluyendo:
 * - Nombre del ingrediente (ej. "Leche", "Harina").
 * - Cantidad necesaria.
 * - Unidad de medida (ej. gramos, litros).
 * - Lista de IDs de las recetas a las que pertenece.
 * - Estado de compra (para uso en la lista de la compra).
 *
 * @property id Identificador único del ingrediente.
 * @property nombre Nombre del ingrediente (ej. "Huevo", "Azúcar").
 * @property cantidad Cantidad necesaria del ingrediente (ej. "200", "1").
 * @property recetaIds Lista de IDs de recetas asociadas a este ingrediente.
 * @property unidadMedida Unidad de medida del ingrediente (ej. "gramos", "litros").
 * @property comprado Indica si el usuario ha marcado este ingrediente como comprado.
 */
data class Ingrediente(
    val id: String = "",
    val nombre: String = "",
    val cantidad: String = "",
    val recetaIds: List<String> = emptyList(),
    val unidadMedida: String = "",
    var comprado: Boolean = false
)
