package com.example.recetalistfct

object userSessionManager{
    var currentUserId: String? = null

    fun updateUser(id: String){
        currentUserId = id
    }
}