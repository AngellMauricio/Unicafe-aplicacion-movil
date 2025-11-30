package com.example.unicafe.Vista.Contract

interface LoginContrac {
    fun mostrarMensaje(mensaje: String)
    fun guardarUsuarioSesion(user_id: Int, rol_id: Int) // Guardamos rol también
    fun navegarACliente() // Ir a Productos
    fun navegarAAdmin()   // Ir a Pedidos
}