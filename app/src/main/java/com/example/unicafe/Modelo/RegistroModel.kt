package com.example.unicafe.Modelo

import com.example.unicafe.Vista.clsDatosRespuesta
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.isNotEmpty
import kotlin.let

class RegistroModel(private val apiService: ifaceApiService) {

    interface OnRegistroListener {
        fun onSuccess(message: String)
        fun onFailure(message: String)
    }

    fun registrarUsuario(nombreUsuario: String, email: String, password: String, telefono: String, listener: OnRegistroListener) {
        apiService.registrarUsuario("registrar", nombreUsuario, email, password, telefono)
            .enqueue(object : Callback<List<clsDatosRespuesta>> {
                override fun onResponse(
                    call: Call<List<clsDatosRespuesta>>,
                    response: Response<List<clsDatosRespuesta>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { datos ->
                            if (datos.isNotEmpty() && datos[0].Estado == "true") {
                                listener.onSuccess(datos[0].Salida)
                            } else {
                                listener.onFailure(datos[0].Salida)
                            }
                        } ?: listener.onFailure("Respuesta vacía o en formato incorrecto")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        listener.onFailure("Error en la respuesta del servidor: $errorBody")
                    }
                }

                override fun onFailure(call: Call<List<clsDatosRespuesta>>, t: Throwable) {
                    listener.onFailure("Error en la conexión: ${t.message}")
                }
            })
    }
}