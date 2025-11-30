package com.example.unicafe.Vista.Contract

interface LoginContrac {
    fun mostrarMensaje(mensaje: String)
    fun guardarUsuarioSesion(user_id: Int)
    fun navegarAMain()
}