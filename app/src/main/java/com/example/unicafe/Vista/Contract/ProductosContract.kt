package com.example.unicafe.Vista.Contract

import com.example.unicafe.Modelo.tblProductos

interface ProductosContract {
    fun mostrarProductos(productos: List<tblProductos>)
    fun mostrarError(mensaje: String)
}