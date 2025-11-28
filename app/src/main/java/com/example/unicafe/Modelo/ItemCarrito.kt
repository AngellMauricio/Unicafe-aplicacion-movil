package com.example.unicafe.Modelo

data class ItemCarrito(
    val producto: tblProductos,
    var cantidad: Int = 1 // Por defecto 1 al agregarlo
) {
    // Propiedad calculada para el subtotal de este ítem
    val subtotal: Double
        get() = producto.precio * cantidad
}
