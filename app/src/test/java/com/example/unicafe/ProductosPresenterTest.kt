package com.example.unicafe

import com.example.unicafe.Modelo.ifaceApiService
import com.example.unicafe.Modelo.tblProductos
import com.example.unicafe.Presentador.ProductosPresenter
import com.example.unicafe.Vista.Contract.ProductosContract
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Field

class ProductosPresenterTest {
    @MockK(relaxed = true)
    private lateinit var view: ProductosContract

    // Este será nuestro "Agente encubierto"
    @MockK
    private lateinit var mockApiService: ifaceApiService

    @MockK(relaxed = true)
    private lateinit var mockCall: Call<List<tblProductos>>

    private lateinit var presenter: ProductosPresenter

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        //Creamos el presenter
        presenter = ProductosPresenter(view)

        // Buscamos la variable privada "apiService" dentro de la clase
        val campoPrivado: Field = ProductosPresenter::class.java.getDeclaredField("apiService")

        campoPrivado.isAccessible = true

        campoPrivado.set(presenter, mockApiService)

        //Configuramos el comportamiento del Mock (Igual que siempre)
        every { mockApiService.obtenerProductos() } returns mockCall
    }
//simula una lista de productos
    @Test
    fun simularListaProductos() {
        // 1. Preparación
        val listaProductos = listOf(
            tblProductos(
                idProducto = 1,
                nombre = "Café Mockeado",
                descripcion = "Descripción de prueba",
                precio = 50.0,
                imagenProdc = ""
            )
        )
        val slotCallback = slot<Callback<List<tblProductos>>>()

        every { mockCall.enqueue(capture(slotCallback)) } answers {
            val response = Response.success(listaProductos)
            slotCallback.captured.onResponse(mockCall, response)
        }

        //Ejecución (Al llamar a esto, usará nuestro Mock inyectado a la fuerza)
        presenter.obtenerProductos()

        //Verificación
        verify { view.mostrarProductos(listaProductos) }
    }
//simula un error en la conexion y en como se carga la lista
    @Test
    fun errorConexionDeLista() {
        val errorMsg = "Fallo de internet simulado"
        val slotCallback = slot<Callback<List<tblProductos>>>()

        every { mockCall.enqueue(capture(slotCallback)) } answers {
            slotCallback.captured.onFailure(mockCall, Throwable(errorMsg))
        }

        presenter.obtenerProductos()

        verify { view.mostrarError("Error: $errorMsg") }
    }
}