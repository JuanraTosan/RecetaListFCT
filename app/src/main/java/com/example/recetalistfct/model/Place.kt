package com.example.recetalistfct.model

data class Place(
    var name: String = "",
    var type: String = "",
    var descripcion: String = "",
    var lat: Double = 0.0,
    var lon: Double = 0.0,
    var rating: Double = 0.0,
    var horaApertura: String = "",
    var horaCierre: String = ""
)