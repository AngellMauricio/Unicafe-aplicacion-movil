package com.example.unicafe.Presentador

import android.content.Context
import com.example.unicafe.Modelo.ItemCarrito
import com.example.unicafe.Modelo.ItemPedido
import com.example.unicafe.Modelo.PedidoRe
import com.example.unicafe.Modelo.clsDatosRespuestaH
import com.example.unicafe.Modelo.ifaceApiService
import com.example.unicafe.Vista.Contract.HistorialContract
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class HistorialPresenter (private val view: HistorialContract.View, private val context: Context) : HistorialContract.Presenter{

    private val apiService: ifaceApiService

    init {
        // Configuración de Retrofit (similar a tus otros presenters)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://unicafe.grupoctic.com/appMovil/api/") // Tu URL base
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        apiService = retrofit.create(ifaceApiService::class.java)
    }

    override fun realizarPedido(listaCarrito: List<ItemCarrito>) {
        view.mostrarCarga()

        // OBTENER EL ID DEL USUARIO ACTUAL
        val sharedPref = context.getSharedPreferences("apiAcceso", Context.MODE_PRIVATE)
        val idUsuarioActual = sharedPref.getInt("idUsuario", -1)

        if (idUsuarioActual == -1) {
            view.ocultarCarga()
            view.mostrarError("Error: No se pudo identificar al usuario. Por favor, inicia sesión nuevamente.")
            return
        }

        // TRANSFORMAR LA LISTA DE ÍTEMS (Igual que antes)
        val listaItemsParaEnviar = listaCarrito.map { itemCarrito ->
            ItemPedido(
                idProducto = itemCarrito.producto.idProducto,
                cantidad = itemCarrito.cantidad,
                precioUnitario = itemCarrito.producto.precio
            )
        }

        // CREAR EL OBJETO DE PETICIÓN COMPLETO
        val peticionCompleta = PedidoRe(
            idUsuario = idUsuarioActual,
            items = listaItemsParaEnviar
        )

        apiService.registrarPedido(peticionCompleta).enqueue(object : Callback<clsDatosRespuestaH> {
            override fun onResponse(call: Call<clsDatosRespuestaH>, response: Response<clsDatosRespuestaH>) {
                view.ocultarCarga()
                if (response.isSuccessful) {
                    val respuesta = response.body()
                    if (respuesta != null) {
                        if (!respuesta.error) {
                            view.mostrarExito(respuesta.mensaje)
                        } else {
                            view.mostrarError(respuesta.mensaje)
                        }
                    } else {
                        view.mostrarError("Respuesta vacía del servidor")
                    }
                } else {
                    view.mostrarError("Error del servidor: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<clsDatosRespuestaH>, t: Throwable) {
                view.ocultarCarga()
                view.mostrarError("Error de conexión: ${t.message}")
            }
        })
    }
}