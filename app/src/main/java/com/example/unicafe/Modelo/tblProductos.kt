package com.example.unicafe.Modelo

data class tblProductos(
    val idProducto : Int,
    val nombre : String,
    val descripcion : String,
    val precio : Double,
    val imagenProdc : String?
)
