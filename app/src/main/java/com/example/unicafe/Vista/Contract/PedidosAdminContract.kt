package com.example.unicafe.Vista.Contract

import com.example.unicafe.Modelo.clsUsuarioPedido

interface PedidosAdminContract {
    interface View {
        fun mostrarUsuarios(lista: List<clsUsuarioPedido>)
        fun mostrarError(mensaje: String)
        fun navegarADetalleUsuario(idUsuario: Int) // Para el futuro
    }
    interface Presenter {
        fun cargarPedidos()
    }
}