package com.example.recetalistfct.controller

import com.example.recetalistfct.model.Ingrediente
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Controlador para gestionar operaciones CRUD sobre ingredientes en Firebase Realtime Database.
 *
 * Este objeto singleton proporciona funciones para:
 * - Guardar, actualizar y eliminar ingredientes.
 * - Obtener listas de ingredientes filtradas por receta o globales.
 *
 * Los datos se almacenan bajo la ruta `/ingrediente` en la base de datos.
 */
object IngredienteController {

    //Referencia a la rama "ingrediente"
    private val database = FirebaseDatabase.getInstance().reference.child("ingrediente")

    /**
     * Guarda un ingrediente en la base de datos.
     *
     * @param ingrediente Objeto `Ingrediente` a guardar.
     * @param onComplete Callback que devuelve `true` si la operación fue exitosa, `false` en caso contrario.
     */
    fun guardarIngrediente(ingrediente: Ingrediente, onComplete: (Boolean) -> Unit) {
        database.child(ingrediente.id).setValue(ingrediente)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    /**
     * Obtiene una lista de ingredientes asociados a una receta específica.
     *
     * Filtra los ingredientes cuya lista `recetaIds` contiene el ID dado.
     *
     * @param recetaId ID de la receta para buscar sus ingredientes.
     * @param onResult Callback que devuelve una lista de ingredientes coincidentes.
     */
    fun obtenerIngredientesPorReceta(recetaId: String, onResult: (List<Ingrediente>) -> Unit) {
        database.orderByChild("recetaIds")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val ingredientes =
                        snapshot.children.mapNotNull { it.getValue(Ingrediente::class.java) }
                            .filter { it.recetaIds.contains(recetaId) }
                    onResult(ingredientes)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

    /**
     * Elimina un ingrediente de la base de datos por su ID.
     *
     * @param id ID único del ingrediente a eliminar.
     * @param onComplete Callback que devuelve `true` si se eliminó correctamente, `false` en caso contrario.
     */
    fun eliminarIngrediente(id: String, onComplete: (Boolean) -> Unit) {
        database.child(id).removeValue()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    /**
     * Obtiene todos los ingredientes almacenados en la base de datos.
     *
     * Ideal para uso global, sin filtro por receta.
     *
     * @param onResult Callback que devuelve una lista con todos los ingredientes disponibles.
     */
    fun obtenerTodos(onResult: (List<Ingrediente>) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = snapshot.children.mapNotNull { it.getValue(Ingrediente::class.java) }
                onResult(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(emptyList())
            }
        })
    }
}