package com.example.unicafe.Presentador


import com.example.unicafe.Modelo.LoginModel
import com.example.unicafe.Vista.Contract.LoginContrac
import kotlin.collections.firstOrNull
import kotlin.text.isEmpty

class LoginPresenter(private val vista: LoginContrac, private val model: LoginModel = LoginModel()) {


    fun iniciarSesion(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            vista.mostrarMensaje("Debe llenar todos los campos")
            return
        }

        model.iniciarSesion(email, password) { datos, error ->
            if (error != null) {
                vista.mostrarMensaje(error)
            } else if (datos != null && datos.firstOrNull()?.Estado == "Correcto") {
                val user = datos.firstOrNull()
                val id = user?.user_id
                val rol = user?.rol_id ?: 3

                if (id != null) {
                    vista.guardarUsuarioSesion(id, rol)

                    if (rol == 3) {
                        vista.navegarACliente()
                    } else if (rol == 1 || rol == 2) {
                        vista.navegarAAdmin()
                    } else {
                        vista.mostrarMensaje("Rol desconocido")
                    }
                }
                // ...
            } else {
                vista.mostrarMensaje("Credenciales incorrectas")
            }
        }
    }
}