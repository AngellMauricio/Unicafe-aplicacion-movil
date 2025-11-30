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
                // 1. Obtenemos el primer objeto de la respuesta
                val respuestaUsuario = datos.firstOrNull()

                // 2. Extraemos el ID usando tu clase clsDatosRespuesta
                //    Usamos el operador seguro (?.) por si es nulo
                val idUsuarioDelServidor = respuestaUsuario?.user_id

                // 3. Verificamos que el ID no sea nulo antes de guardarlo
                if (idUsuarioDelServidor != null) {
                    // Llamamos a la nueva función de la vista para guardar el ID
                    vista.guardarUsuarioSesion(idUsuarioDelServidor)
                    vista.mostrarMensaje("Login exitoso. ID: $idUsuarioDelServidor") // Opcional: para depurar
                } else {
                    vista.mostrarMensaje("Login correcto pero no se recibió el ID de usuario.")
                }

                // 4. Navegamos a la pantalla principal
                vista.navegarAMain()
            } else {
                vista.mostrarMensaje("Usuario o contraseña incorrectos")
            }
        }
    }
}