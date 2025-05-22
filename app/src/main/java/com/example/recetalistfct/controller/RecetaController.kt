package com.example.recetalistfct.controller

import android.net.Uri
import android.util.Log
import com.example.recetalistfct.model.Receta
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

/**
 * Controlador para gestionar operaciones relacionadas con recetas en Firebase.
 *
 * Este objeto singleton permite:
 * - Subir imágenes de recetas (principal y galería).
 * - Guardar, actualizar y eliminar recetas en la base de datos.
 * - Obtener listas de recetas por usuario o globalmente.
 */
object RecetaController {

    private val database = FirebaseDatabase.getInstance().reference
    private val storage = FirebaseStorage.getInstance().reference

    /**
     * Sube una imagen principal de receta y devuelve su URL de descarga.
     *
     * @param uri URI local del archivo de imagen seleccionado.
     * @param uid ID del usuario actual (para organizar las imágenes por usuario).
     * @param onComplete Callback que devuelve la URL de descarga si fue exitoso, `null` en caso contrario.
     */
    fun subirImagenReceta(uri: Uri, uid: String, onComplete: (String?) -> Unit) {
        val fileName = "fotos-recetas/$uid-${UUID.randomUUID()}.jpg"
        val ref = storage.child(fileName)

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

    /**
     * Sube múltiples imágenes para la galería de una receta y devuelve sus URLs.
     *
     * Si alguna imagen falla, continúa subiendo las demás y devuelve solo las que se subieron correctamente.
     *
     * @param uris Lista de URIs locales de las imágenes seleccionadas.
     * @param uid ID del usuario actual (para organizar las imágenes por usuario).
     * @param onComplete Callback que devuelve una lista de URLs de descarga.
     */
    fun subirMultiplesImagenesReceta(
        uris: List<Uri>,
        uid: String,
        onComplete: (List<String>) -> Unit
    ) {
        val urls = mutableListOf<String>()
        var uploadedCount = 0

        if (uris.isEmpty()) {
            onComplete(emptyList())
            return
        }

        uris.forEach { uri ->
            val fileName = "galeria-recetas/$uid-${UUID.randomUUID()}.jpg"
            val imageRef = storage.child(fileName)

            imageRef.putFile(uri).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                imageRef.downloadUrl
            }.addOnSuccessListener { downloadUri ->
                urls.add(downloadUri.toString())
                uploadedCount++
                if (uploadedCount == uris.size) {
                    onComplete(urls)
                }
            }.addOnFailureListener {
                uploadedCount++
                if (uploadedCount == uris.size) {
                    onComplete(urls) // Devuelve las que sí se subieron
                }
            }
        }
    }

    /**
     * Guarda o actualiza una receta en Firebase Realtime Database.
     *
     * @param receta Objeto `Receta` a guardar.
     * @param onComplete Callback que devuelve `true` si tuvo éxito, `false` en caso contrario.
     */
    fun guardarReceta(receta: Receta, onComplete: (Boolean) -> Unit) {
        database.child("receta").child(receta.id)
            .setValue(receta)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    /**
     * Obtiene todas las recetas creadas por un usuario específico.
     *
     * @param uid ID único del usuario cuyas recetas queremos cargar.
     * @param onResult Callback que devuelve la lista de recetas encontradas.
     */
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

    /**
     * Obtiene una receta específica por su ID desde Firebase.
     *
     * @param recetaId ID único de la receta.
     * @param onResult Callback que devuelve la receta si existe, `null` en caso contrario.
     */
    fun obtenerRecetaPorId(recetaId: String, onResult: (Receta?) -> Unit) {
        val recetaRef = database.child("receta").child(recetaId)

        recetaRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val receta = snapshot.getValue(Receta::class.java)
                onResult(receta)
            } else {
                onResult(null)
            }
        }.addOnFailureListener { exception ->
            onResult(null)
            Log.e("Firebase", "Error al obtener receta: ${exception.message}")
        }
    }

    /**
     * Elimina una receta de la base de datos por su ID.
     *
     * @param idReceta ID único de la receta a eliminar.
     * @param onResult Callback que devuelve `true` si se eliminó correctamente, `false` en caso contrario.
     */
    fun eliminarReceta(idReceta: String, onResult: (Boolean) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("receta/$idReceta")
        ref.removeValue()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    /**
     * Obtiene todas las recetas disponibles en la aplicación.
     *
     * Ideal para mostrar una lista global de recetas sin filtro por usuario.
     *
     * @param onResult Callback que devuelve una lista con todas las recetas cargadas.
     */
    fun obtenerTodasLasRecetas(onResult: (List<Receta>) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("receta")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
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
}