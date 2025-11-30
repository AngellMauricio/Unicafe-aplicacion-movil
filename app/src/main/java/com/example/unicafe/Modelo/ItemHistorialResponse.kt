package com.example.unicafe.Modelo

data class ItemHistorialResponse(
    val idProducto: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenProdc: String,
    val cantidad: Int,
    val subtotal: Double
)
