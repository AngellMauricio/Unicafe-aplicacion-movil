package com.example.unicafe.Presentador

import android.content.Context
import com.example.unicafe.Modelo.ItemCarrito
import com.example.unicafe.Modelo.ItemHistorialResponse
import com.example.unicafe.Modelo.ItemPedido
import com.example.unicafe.Modelo.PedidoRe
import com.example.unicafe.Modelo.clsDatosRespuestaH
import com.example.unicafe.Modelo.ifaceApiService
import com.example.unicafe.Modelo.tblProductos
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
        // Configuración de Retrofit
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

    override fun cargarHistorialDeUsuario(idUsuario: Int) {
        view.mostrarCarga()

        apiService.obtenerPedidosPorUsuario(idUsuario).enqueue(object : Callback<List<ItemHistorialResponse>> {
            override fun onResponse(call: Call<List<ItemHistorialResponse>>, response: Response<List<ItemHistorialResponse>>) {
                view.ocultarCarga()
                if (response.isSuccessful && response.body() != null) {
                    val listaRespuesta = response.body()!!

                    // Convertimos la respuesta del API a 'ItemCarrito' para poder
                    // reutilizar el mismo Adaptador que ya se tiene.
                    val listaConvertida = listaRespuesta.map { itemApi ->
                        // Reconstruimos el producto
                        val productoReconstruido = tblProductos(
                            idProducto = itemApi.idProducto,
                            nombre = itemApi.nombre,
                            descripcion = itemApi.descripcion,
                            precio = itemApi.precio,
                            imagenProdc = itemApi.imagenProdc
                        )
                        // Creamos el ItemCarrito
                        ItemCarrito(productoReconstruido, itemApi.cantidad)
                    }

                    // Enviamos la lista convertida a la vista
                    view.mostrarListaHistorial(listaConvertida)

                } else {
                    view.mostrarError("No se encontraron pedidos para este usuario.")
                }
            }

            override fun onFailure(call: Call<List<ItemHistorialResponse>>, t: Throwable) {
                view.ocultarCarga()
                view.mostrarError("Error de conexión al cargar historial: ${t.message}")
            }
        })
    }
    override fun realizarPedido(listaCarrito: List<ItemCarrito>) {
        view.mostrarCarga()

        val sharedPref = context.getSharedPreferences("MiAppPreferenciasGlobales", Context.MODE_PRIVATE)
        // Usamos LA MISMA clave que en login.kt ("user_id")
        val idUsuarioActual = sharedPref.getInt("user_id", -1)


        if (idUsuarioActual == -1) {
            view.ocultarCarga()
            view.mostrarError("Error: No se pudo identificar al usuario. Por favor, inicia sesión nuevamente.")
            return
        }

        // 2. TRANSFORMAR LA LISTA DE ÍTEMS (Igual que antes)
        val listaItemsParaEnviar = listaCarrito.map { itemCarrito ->
            ItemPedido(
                idProducto = itemCarrito.producto.idProducto,
                cantidad = itemCarrito.cantidad,
                precioUnitario = itemCarrito.producto.precio,
                subtotal = itemCarrito.subtotal
            )
        }

        // 3. CREAR EL OBJETO DE PETICIÓN COMPLETO
        val peticionCompleta = PedidoRe(
            idUsuario = idUsuarioActual,
            items = listaItemsParaEnviar
        )

        // 4. ENVIAR ('peticionCompleta')
        apiService.registrarPedido(peticionCompleta).enqueue(object : Callback<clsDatosRespuestaH> {
            // ... (El resto del onResponse y onFailure sigue IGUAL que en la respuesta anterior) ...
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