package com.example.unicafe.Presentador

import com.example.unicafe.Modelo.RegistroModel
import com.example.unicafe.Vista.Contract.RegistroContrac

class RegistrPresenter (
private val vista: RegistroContrac,
private val model: RegistroModel
) {
    fun registrarUsuario(nombreUsuario: String, email: String, password: String) {
        model.registrarUsuario(nombreUsuario, email, password, object : RegistroModel.OnRegistroListener {
            override fun onSuccess(message: String) {
                vista.mostrarMensaje(message)
                vista.registroExitoso()
            }

            override fun onFailure(message: String) {
                vista.mostrarMensaje(message)
            }
        })
    }
}