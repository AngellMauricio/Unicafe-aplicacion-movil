package com.example.unicafe.Vista.Contract

import com.example.unicafe.Modelo.ItemCarrito

interface HistorialContract {
    // Lo que la Vista hace
    interface View {
        fun mostrarCarga()
        fun ocultarCarga()
        fun mostrarExito(mensaje: String)
        fun mostrarError(mensaje: String)

        // NUEVO: Mostrar lista cargada desde API (Modo Admin)
        fun mostrarListaHistorial(lista: List<ItemCarrito>)
    }

    // Lo que el Presentador hace
    interface Presenter {
        fun realizarPedido(listaCarrito: List<ItemCarrito>)

        // NUEVO: Cargar historial de un usuario específico (Modo Admin)
        fun cargarHistorialDeUsuario(idUsuario: Int)
    }
}