package com.example.unicafe.Modelo

data class clsUsuarioPedido(
    val idUsuario: Int,
    val nombreUsuario: String,
    val telefono: String,
    val totalAcumulado: Double,
    val cantidadPedidos: Int
)
