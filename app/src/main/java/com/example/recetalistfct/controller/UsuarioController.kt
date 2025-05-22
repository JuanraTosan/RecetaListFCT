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

/**
 * Controlador para gestionar operaciones relacionadas con usuarios en Firebase.
 *
 * Este objeto singleton permite:
 * - Registrar nuevos usuarios.
 * - Iniciar sesión con correo y contraseña.
 * - Obtener o actualizar datos del usuario.
 * - Subir foto de perfil a Firebase Storage.
 */
object UsuarioController {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db =FirebaseDatabase.getInstance().reference.child("usuario")
    private val storage = FirebaseStorage.getInstance()

    /**
     * Registra un nuevo usuario en Firebase Authentication y lo guarda en la base de datos.
     *
     * También almacena localmente el ID del usuario en SharedPreferences.
     *
     * @param username Nombre de usuario elegido por el usuario.
     * @param email Dirección de correo electrónico.
     * @param password Contraseña del usuario.
     * @param onSuccess Callback que devuelve el objeto `Usuario` si el registro es exitoso.
     * @param onError Callback que devuelve un mensaje de error si algo falla.
     * @param context Contexto usado para acceder a SharedPreferences.
     */
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

    /**
     * Inicia sesión de usuario con correo y contraseña.
     *
     * Almacena localmente el UID del usuario si tiene éxito.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param context Contexto usado para acceder a SharedPreferences.
     * @param onSuccess Callback que devuelve el objeto `Usuario` si el login fue exitoso.
     * @param onError Callback que devuelve un mensaje de error si algo falla.
     */
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

    /**
     * Obtiene los datos de un usuario desde Firebase Realtime Database.
     *
     * @param uid ID único del usuario cuyos datos queremos obtener.
     * @param onComplete Callback que devuelve el objeto `Usuario` o `null` si no se encontró.
     */
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

    /**
     * Actualiza los datos del perfil del usuario en Firebase Realtime Database.
     *
     * @param usuario Objeto `Usuario` con los nuevos datos actualizados.
     */
    fun actualizarPerfil(usuario: Usuario){
       db.child(usuario.uid).setValue(usuario)
            .addOnSuccessListener {
                Log.d("RealtimeDB", "Actualizacion exitosa")
            }
            .addOnFailureListener{e ->
                Log.e("RealtimeDB", "Error al actualizar perfil: ${e.localizedMessage}")
            }
    }

    /**
     * Sube una nueva foto de perfil del usuario a Firebase Storage.
     *
     * @param imagenUri URI local de la imagen seleccionada.
     * @param uid ID del usuario cuya foto se está subiendo.
     * @param onComplete Callback que devuelve la URL de descarga si tuvo éxito, `null` en caso contrario.
     */
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

    /**
     * Obtiene los datos de un usuario específico desde Firebase Realtime Database.
     *
     * @param uid ID único del usuario.
     * @param onResult Callback que devuelve el objeto `Usuario` o `null` si no existe.
     */
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