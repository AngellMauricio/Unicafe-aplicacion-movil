package com.example.unicafe.Vista.Contract

import com.example.unicafe.Modelo.ItemCarrito

interface HistorialContract {
    // Lo que la Vista hace
    interface View {
        fun mostrarCarga() // Deshabilitar botón, mostrar progress bar
        fun ocultarCarga() // Habilitar botón
        fun mostrarExito(mensaje: String) // Limpiar lista y mostrar toast
        fun mostrarError(mensaje: String)
    }

    // Lo que el Presentador hace
    interface Presenter {
        // Recibe la lista del carrito interno de la app
        fun realizarPedido(listaCarrito: List<ItemCarrito>)
    }
}