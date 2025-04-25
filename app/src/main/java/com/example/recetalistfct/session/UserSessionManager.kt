package com.example.recetalistfct.session

object userSessionManager{
    var currentUserId: String? = null

    fun updateUser(id: String){
        currentUserId = id
    }
}