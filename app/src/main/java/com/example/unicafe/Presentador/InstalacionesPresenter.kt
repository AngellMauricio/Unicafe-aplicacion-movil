package com.example.unicafe.Presentador

import com.example.unicafe.Modelo.tblVideos
import com.example.unicafe.Vista.Contract.InstalacionesContract
import com.example.unicafe.Modelo.ifaceApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InstalacionesPresenter(private val view: InstalacionesContract) {
    private val apiService: ifaceApiService

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://unicafe.grupoctic.com/appMovil/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        apiService = retrofit.create(ifaceApiService::class.java)
    }
    fun obtenerVideos() {
        apiService.obtenerVideos().enqueue(object : Callback<List<tblVideos>> {

            override fun onResponse(call: Call<List<tblVideos>>, response: Response<List<tblVideos>>) {
                if (response.isSuccessful) {
                    response.body()?.let { videos ->
                        view.mostrarVideos(videos)
                    } ?: run {
                        view.mostrarError("La lista de videos está vacía o hubo un error de formato.")
                    }
                } else {
                    view.mostrarError("Error del servidor: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<tblVideos>>, t: Throwable) {
                view.mostrarError("Error de conexión: ${t.message}")
            }
        })
    }
}