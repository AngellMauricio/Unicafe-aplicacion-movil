package com.example.unicafe.Vista.Contract

import com.example.unicafe.Modelo.tblVideos

interface InstalacionesContract {
    fun mostrarVideos(videos: List<tblVideos>)
    fun mostrarError(mensaje: String)
}