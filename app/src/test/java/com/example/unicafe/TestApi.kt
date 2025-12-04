package com.example.unicafe

import android.util.Log
import com.example.unicafe.Modelo.clsDatosRespuestaH
import com.example.unicafe.Modelo.ifaceApiService
import com.example.unicafe.Vista.clsDatosRespuesta
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.test.Test

class TestApi {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://unicafe.grupoctic.com/appMovil/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ifaceApiService::class.java)
    @Test
    fun probarLogin(email: String, password: String) {

        api.iniciarSesion("login", email, password)
            .enqueue(object : Callback<List<clsDatosRespuesta>> {

                override fun onResponse(
                    call: Call<List<clsDatosRespuesta>>,
                    response: Response<List<clsDatosRespuesta>>
                ) {
                    Log.d("TEST_API", "LOGIN => ${response.body()}")
                }

                override fun onFailure(
                    call: Call<List<clsDatosRespuesta>>,
                    t: Throwable
                ) {
                    Log.e("TEST_API", "ERROR LOGIN => ${t.message}")
                }
            })
    }
}




