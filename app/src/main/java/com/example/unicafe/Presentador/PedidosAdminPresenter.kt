package com.example.unicafe.Presentador

import com.example.unicafe.Modelo.clsUsuarioPedido
import com.example.unicafe.Modelo.ifaceApiService
import com.example.unicafe.Vista.Contract.PedidosAdminContract
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PedidosAdminPresenter(private val view: PedidosAdminContract.View) : PedidosAdminContract.Presenter {

    private val apiService: ifaceApiService

    init {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://unicafe.grupoctic.com/appMovil/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        apiService = retrofit.create(ifaceApiService::class.java)
    }

    override fun cargarPedidos() {
        apiService.obtenerUsuariosConPedidos().enqueue(object : Callback<List<clsUsuarioPedido>> {
            override fun onResponse(call: Call<List<clsUsuarioPedido>>, response: Response<List<clsUsuarioPedido>>) {
                if (response.isSuccessful && response.body() != null) {
                    view.mostrarUsuarios(response.body()!!)
                } else {
                    view.mostrarError("No hay pedidos o error de servidor")
                }
            }
            override fun onFailure(call: Call<List<clsUsuarioPedido>>, t: Throwable) {
                view.mostrarError("Error de conexión: ${t.message}")
            }
        })
    }
}