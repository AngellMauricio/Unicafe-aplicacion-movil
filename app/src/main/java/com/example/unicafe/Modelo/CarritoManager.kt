package com.example.unicafe.Modelo

object CarritoManager {
    val itemsCarrito = mutableListOf<ItemCarrito>()

    fun agregarProducto(productoNuevo: tblProductos) {
        val itemExistente = itemsCarrito.find { it.producto.idProducto == productoNuevo.idProducto }

        if (itemExistente != null) {
            itemExistente.cantidad++
        } else {
            itemsCarrito.add(ItemCarrito(productoNuevo))
        }
    }

    fun obtenerTotalPedido(): Double {
        var total = 0.0
        for (item in itemsCarrito) {
            total += item.subtotal
        }
        return total
    }

    fun limpiarCarrito() {
        itemsCarrito.clear()
    }
}