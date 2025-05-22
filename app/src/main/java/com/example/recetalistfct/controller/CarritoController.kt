package com.example.recetalistfct.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.recetalistfct.model.Ingrediente
import com.example.recetalistfct.model.ListaCompras
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

/**
 * Controlador para gestionar la lista de compras del usuario.
 *
 * Este objeto singleton permite:
 * - Cargar y guardar listas de compras en Firebase Realtime Database.
 * - Añadir, eliminar o modificar ingredientes localmente.
 * - Guardar cambios automáticamente en la base de datos.
 * - Escuchar cambios en la autenticación para actualizar datos por usuario.
 */
object CarritoController {
    private val database = FirebaseDatabase.getInstance().reference
    private var auth = FirebaseAuth.getInstance()
    private var currentUid by mutableStateOf<String>("")

    val ingredientes = mutableStateListOf<Ingrediente>()
    val listaNombre = mutableStateOf("")


    /**
     * Inicialización del controlador.
     *
     * - Obtiene el UID inicial del usuario.
     * - Registra un listener para detectar cambios de sesión (login/logout).
     */
    init {
        updateCurrentUid()
        auth.addAuthStateListener { auth ->
            updateCurrentUid()
        }
    }

    private fun updateCurrentUid() {
        currentUid = auth.currentUser?.uid ?: ""
    }

    /**
     * Carga la lista de compras asociada a un usuario desde Firebase.
     *
     * @param uid ID único del usuario cuya lista se va a cargar.
     * @param onResult Callback que recibe la lista cargada o null si no existe.
     */
    fun cargarListaComprasDeUsuario(uid: String, onResult: (ListaCompras?) -> Unit) {
        if (uid.isBlank()) {
            onResult(null)
            return
        }

        database.child("listasCompras").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(ListaCompras::class.java)?.let { lista ->
                    onResult(lista)
                    ingredientes.clear()
                    ingredientes.addAll(lista.ingredientes)
                    listaNombre.value = lista.nombre
                } ?: run {
                    onResult(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(null)
            }
        })
    }

    /**
     * Guarda una lista de compras completa en Firebase.
     *
     * @param lista Objeto `ListaCompras` a guardar.
     * @param onFinish Callback que indica si la operación fue exitosa (`true`) o falló (`false`).
     */
    fun guardarLista(lista: ListaCompras, onFinish: (Boolean) -> Unit) {

        database.child("listasCompras").child(currentUid).setValue(lista)
            .addOnCompleteListener { task ->
                onFinish(task.isSuccessful)
            }
    }

    /**
     * Marca o desmarca un ingrediente como "comprado".
     *
     * @param index Índice del ingrediente dentro de la lista.
     */
    fun toggleComprado(index: Int) {
        val nuevosIngredientes = ingredientes.toMutableList().apply {
            set(index, this[index].copy(comprado = !this[index].comprado))
        }
        ingredientes.clear()
        ingredientes.addAll(nuevosIngredientes)
    }

    /**
     * Elimina todos los ingredientes marcados como comprados de la lista actual.
     *
     * Llama automáticamente a `guardarCambios()` después de la eliminación.
     */
    fun eliminarSeleccionados() {
        val nuevosIngredientes = ingredientes.filterNot { it.comprado }
        ingredientes.clear()
        ingredientes.addAll(nuevosIngredientes)
        guardarCambios()
    }

    /**
     * Añade un nuevo ingrediente al carrito y guarda los cambios en Firebase.
     *
     * @param ingrediente Ingrediente a añadir.
     */
    fun anadirIngrediente(ingrediente: Ingrediente) {
        ingredientes.add(ingrediente)
        guardarCambios()
    }

    /**
     * Añade múltiples ingredientes al carrito y guarda los cambios en Firebase.
     *
     * @param nuevos Ingredientes a añadir.
     */
    fun anadirIngredientes(vararg nuevos: Ingrediente) {
        ingredientes.addAll(nuevos)
        guardarCambios()
    }

    /**
     * Limpia completamente la lista de ingredientes local.
     */
    fun limpiarIngredientes() {
        ingredientes.clear()
    }

    /**
     * Guarda los cambios realizados en la lista local de ingredientes en Firebase.
     *
     * Si no hay usuario autenticado, no se realiza ninguna acción.
     */
    fun guardarCambios() {
        if (currentUid.isBlank()) return

        val nuevaLista = ListaCompras(
            id = currentUid,
            nombre = listaNombre.value,
            ingredientes = ingredientes.toList(),
            usuarioId = currentUid
        )

        guardarLista(nuevaLista){}
    }
}