package com.example.unicafe.Modelo

data class PedidoRe(
    val idUsuario: Int,
    val items: List<ItemPedido> // Reutilizamos la clase pequeña que ya tenías para los ítems
)
