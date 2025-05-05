package com.example.recetalistfct.controller

import android.net.Uri
import com.example.recetalistfct.model.Receta
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

object RecetaController {

    private val database = FirebaseDatabase.getInstance().reference
    private val storage = FirebaseStorage.getInstance()

    fun subirImagenReceta(uri: Uri, uid: String, onComplete: (String?) -> Unit) {
        val fileName = "fotos-recetas/$uid-${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(fileName)

        ref.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception!!
                ref.downloadUrl
            }.addOnSuccessListener { downloadUri ->
                onComplete(downloadUri.toString())
            }.addOnFailureListener {
                onComplete(null)
            }
    }

    fun guardarReceta(receta: Receta, onComplete: (Boolean) -> Unit) {
        database.child("receta").child(receta.id)
            .setValue(receta)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun obtenerRecetasDeUsuario(uid: String, onResult: (List<Receta>) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("receta")
        dbRef.orderByChild("usuarioId").equalTo(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val recetas = mutableListOf<Receta>()
                    for (recetaSnapshot in snapshot.children) {
                        val receta = recetaSnapshot.getValue(Receta::class.java)
                        receta?.let {
                            recetas.add(it.copy(id = recetaSnapshot.key ?: ""))
                        }
                    }
                    onResult(recetas)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

    fun eliminarReceta(idReceta: String, onResult: (Boolean) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("receta/$idReceta")
        ref.removeValue()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}