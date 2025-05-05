package com.example.recetalistfct.controller

import com.example.recetalistfct.model.Ingrediente
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object IngredienteController {

    private val database = FirebaseDatabase.getInstance().reference.child("ingrediente")

    // Guardar ingrediente (global, si decides almacenarlos fuera de la receta)
    fun guardarIngrediente(ingrediente: Ingrediente, onComplete: (Boolean) -> Unit) {
        database.child(ingrediente.id).setValue(ingrediente)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Obtener ingredientes de una receta (filtrando por recetaId en recetaIds)
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

    // Eliminar ingrediente por ID (en la rama global de ingredientes)
    fun eliminarIngrediente(id: String, onComplete: (Boolean) -> Unit) {
        database.child(id).removeValue()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Obtener todos los ingredientes globales
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