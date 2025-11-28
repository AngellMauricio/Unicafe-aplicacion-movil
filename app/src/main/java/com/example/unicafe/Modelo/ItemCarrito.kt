package com.example.unicafe.Modelo

data class ItemCarrito(
    val producto: tblProductos,
    var cantidad: Int = 1
) {
    val subtotal: Double
        get() = producto.precio * cantidad
}
