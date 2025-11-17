package com.example.unicafe.Modelo


import com.example.unicafe.Vista.clsDatosRespuesta
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

class LoginModel {
    private val apiService: ifaceApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://unicafe.grupoctic.com/appMovil/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ifaceApiService::class.java)
    }

    fun iniciarSesion(email: String, password: String, callback: (List<clsDatosRespuesta>?, String?) -> Unit) {
        apiService.iniciarSesion("login", email, password)
            .enqueue(object : Callback<List<clsDatosRespuesta>> {
                override fun onResponse(
                    call: Call<List<clsDatosRespuesta>>,
                    response: Response<List<clsDatosRespuesta>>
                ) {
                    if (response.isSuccessful) {
                        callback(response.body(), null)
                    } else {
                        callback(null, "Error en la respuesta del servidor: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<clsDatosRespuesta>>, t: Throwable) {
                    callback(null, "Error en la conexión: ${t.message}")
                }
            })
    }
}