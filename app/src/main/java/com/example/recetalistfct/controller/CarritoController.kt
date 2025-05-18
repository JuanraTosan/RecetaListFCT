package com.example.recetalistfct.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.recetalistfct.model.Ingrediente
import com.example.recetalistfct.model.ListaCompras
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


object CarritoController {
    private val database = FirebaseDatabase.getInstance().reference
    private var auth = FirebaseAuth.getInstance()
    private var currentUid by mutableStateOf<String>("")

    val ingredientes = mutableStateListOf<Ingrediente>()
    val listaNombre = mutableStateOf("")


    // Inicializamos el UID actual y escuchamos cambios de autenticación
    init {
        updateCurrentUid()
        auth.addAuthStateListener { auth ->
            updateCurrentUid()
        }
    }

    private fun updateCurrentUid() {
        currentUid = auth.currentUser?.uid ?: ""
    }

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

    fun guardarLista(lista: ListaCompras, onFinish: (Boolean) -> Unit) {

        database.child("listasCompras").child(currentUid).setValue(lista)
            .addOnCompleteListener { task ->
                onFinish(task.isSuccessful)
            }
    }

    fun toggleComprado(index: Int) {
        val nuevosIngredientes = ingredientes.toMutableList().apply {
            set(index, this[index].copy(comprado = !this[index].comprado))
        }
        ingredientes.clear()
        ingredientes.addAll(nuevosIngredientes)
    }


    fun eliminarSeleccionados() {
        val nuevosIngredientes = ingredientes.filterNot { it.comprado }
        ingredientes.clear()
        ingredientes.addAll(nuevosIngredientes)
        guardarCambios()
    }

    fun anadirIngrediente(ingrediente: Ingrediente) {
        ingredientes.add(ingrediente)
        guardarCambios()
    }

    fun anadirIngredientes(vararg nuevos: Ingrediente) {
        ingredientes.addAll(nuevos)
        guardarCambios()
    }

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