package com.example.recetalistfct.controller

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.recetalistfct.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


object UsuarioController {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db =FirebaseDatabase.getInstance().reference.child("usuario")
    private val storage = FirebaseStorage.getInstance()



    fun registrarUsuario(
        username: String,
        email: String,
        password: String,
        onSuccess: (Usuario) -> Unit,
        onError: (String) -> Unit,
        context: Context
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                val usuario = Usuario(username, email, uid)

                db.child(uid).setValue(usuario)
                    .addOnSuccessListener {
                    // Guardar en SharedPreferences
                    context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
                        .edit().putString("usuarioId", uid).apply()
                        onSuccess(usuario)
                    }
                    .addOnFailureListener {
                    onError("Error al guardar en Realtime DB: ${it.localizedMessage}")
                }
            }.addOnFailureListener {
                onError("Error de registro: ${it.localizedMessage}")
            }
    }

    fun loginUsuario(
        email: String,
        password: String,
        context: Context,
        onSuccess: (Usuario) -> Unit,
        onError: (String) -> Unit
    ){
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener

                db.child(uid).get()
                    .addOnSuccessListener { snapshot ->
                        val usuario = snapshot.getValue(Usuario::class.java)
                        if (usuario != null){
                            context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
                                .edit().putString("usuarioId", uid).apply()
                            onSuccess(usuario)
                        }else{
                            onError("Usuario no encontrado")
                        }
                    }
                    .addOnFailureListener{
                        onError("Error al recuperar datos del usuario")
                    }
            }
            .addOnFailureListener{
                onError("Error de login: ${it.localizedMessage}")
            }
    }

    fun obtenerUsuario(uid: String, onComplete: (Usuario?) -> Unit){
        db.child(uid).get()
            .addOnSuccessListener { snapshot ->
                val usuario = snapshot.getValue(Usuario::class.java)
                onComplete(usuario)
            }
            .addOnFailureListener{ e ->
                Log.e("RealtimeDB", "Error al obtener usuario: ${e.localizedMessage}")
                onComplete(null)
            }
    }

    fun actualizarPerfil(usuario: Usuario){
       db.child(usuario.uid).setValue(usuario)
            .addOnSuccessListener {
                Log.d("RealtimeDB", "Actualizacion exitosa")
            }
            .addOnFailureListener{e ->
                Log.e("RealtimeDB", "Error al actualizar perfil: ${e.localizedMessage}")
            }
    }

    // Método para subir la foto de perfil a Firebase Storage y obtener la URL
    fun subirFotoPerfil(imagenUri: Uri, uid: String, onComplete: (String?) -> Unit) {
        val filename = "fotos_perfil/$uid-${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(filename)

        ref.putFile(imagenUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception!!
                ref.downloadUrl
            }.addOnSuccessListener { uriDescarga ->
                onComplete(uriDescarga.toString())
            }.addOnFailureListener {
                onComplete(null)
            }
    }

    fun obtenerUsuarioPorId(uid: String, onResult: (Usuario?) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        val usuariosRef = database.child("usuario").child(uid)

        usuariosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario = snapshot.getValue(Usuario::class.java)
                onResult(usuario)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("obtenerUsuarioPorId", "Error: ${error.message}")
                onResult(null)
            }
        })
    }
}