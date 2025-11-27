package com.example.unicafe.Presentador

import com.example.unicafe.Modelo.ifaceApiService
import com.example.unicafe.Modelo.tblProductos
import com.example.unicafe.Vista.Contract.ProductosContract
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


class ProductosPresenter(private val view: ProductosContract) {
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

    fun obtenerProductos() {
        apiService.obtenerProductos().enqueue(object : Callback<List<tblProductos>> {
            override fun onResponse(call: Call<List<tblProductos>>, response: Response<List<tblProductos>>) {
                if (response.isSuccessful) {
                    response.body()?.let { productos ->
                        view.mostrarProductos(productos)
                    } ?: run {
                        view.mostrarError("Error: ${response.message()}" )
                    }
                }
                else {
                    view.mostrarError("Error: ${response.message()}" )
                }
            }

            override fun onFailure(call : Call<List<tblProductos>>, t: Throwable) {
                view.mostrarError("Error: ${t.message}")
            }
        })
    }
}