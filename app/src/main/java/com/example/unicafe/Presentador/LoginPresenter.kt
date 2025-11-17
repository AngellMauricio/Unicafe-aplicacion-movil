package com.example.unicafe.Presentador


import com.example.unicafe.Modelo.LoginModel
import com.example.unicafe.Vista.Contract.LoginContrac
import kotlin.collections.firstOrNull
import kotlin.text.isEmpty

class LoginPresenter(private val vista: LoginContrac) {
    private val model = LoginModel()

    fun iniciarSesion(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            vista.mostrarMensaje("Debe llenar todos los campos")
            return
        }

        model.iniciarSesion(email, password) { datos, error ->
            if (error != null) {
                vista.mostrarMensaje(error)
            } else if (datos != null && datos.firstOrNull()?.Estado == "Correcto") {
                vista.navegarAMain()
            } else {
                vista.mostrarMensaje("Usuario o contraseña incorrectos")
            }
        }
    }
}