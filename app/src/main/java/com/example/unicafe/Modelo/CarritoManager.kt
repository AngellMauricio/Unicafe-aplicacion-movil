package com.example.unicafe.Modelo

object CarritoManager {
    // Esta lista mantendrá los productos seleccionados en memoria
    val itemsCarrito = mutableListOf<ItemCarrito>()

    // Función para agregar un producto. Si ya existe, aumenta la cantidad.
    fun agregarProducto(productoNuevo: tblProductos) {
        // Buscamos si el producto ya está en la lista por su ID
        val itemExistente = itemsCarrito.find { it.producto.idProducto == productoNuevo.idProducto }

        if (itemExistente != null) {
            // Si existe, aumentamos la cantidad
            itemExistente.cantidad++
        } else {
            // Si no existe, lo agregamos nuevo con cantidad 1
            itemsCarrito.add(ItemCarrito(productoNuevo))
        }
    }

    // Función para calcular el total de todo el pedido
    fun obtenerTotalPedido(): Double {
        var total = 0.0
        for (item in itemsCarrito) {
            total += item.subtotal
        }
        return total
        // Forma más "Kotlin" de hacerlo en una línea:
        // return itemsCarrito.sumOf { it.subtotal }
    }

    // Limpiar el carrito después de enviar el pedido
    fun limpiarCarrito() {
        itemsCarrito.clear()
    }
}