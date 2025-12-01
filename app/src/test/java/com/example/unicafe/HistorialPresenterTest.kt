package com.example.unicafe

import android.content.Context
import android.content.SharedPreferences
import com.example.unicafe.Modelo.*
import com.example.unicafe.Presentador.HistorialPresenter
import com.example.unicafe.Vista.Contract.HistorialContract
import io.mockk.*
import io.mockk.impl.annotations.MockK
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Field

class HistorialPresenterTest {
    // --- MOCKS ---
    @MockK(relaxed = true)
    private lateinit var view: HistorialContract.View

    @MockK
    private lateinit var mockContext: Context

    @MockK
    private lateinit var mockSharedPrefs: SharedPreferences

    @MockK
    private lateinit var mockApiService: ifaceApiService

    // Variable para el presenter real
    private lateinit var presenter: HistorialPresenter

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        // 1. CONFIGURACIÓN DEL CONTEXTO (Mock Normal)
        // Cuando el presenter pida las preferencias al contexto, le damos las falsas
        every {
            mockContext.getSharedPreferences("MiAppPreferenciasGlobales", Context.MODE_PRIVATE)
        } returns mockSharedPrefs

        // 2. CREACIÓN DEL PRESENTER
        // Pasamos el contexto mockeado legalmente por el constructor
        presenter = HistorialPresenter(view, mockContext)

        // 3. INYECCIÓN DEL API SERVICE (Reflexión / Hack) 💉
        try {
            val campoPrivado: Field = HistorialPresenter::class.java.getDeclaredField("apiService")
            campoPrivado.isAccessible = true
            campoPrivado.set(presenter, mockApiService)
        } catch (e: Exception) {
            println("Error en reflexión: Asegúrate de que la variable se llame 'apiService'")
        }
    }

    //simula una lista correctamente cargada
    @Test
    fun listaCorrectaAlCargar() {
        //Preparación de datos falsos del API
        val itemApi = ItemHistorialResponse(
            idProducto = 1,
            nombre = "Café Test",
            descripcion = "Desc",
            precio = 25.0,
            imagenProdc = "",
            cantidad = 2,
            subtotal = 50.0
        )
        val listaRespuesta = listOf(itemApi)


        val mockCall = mockk<Call<List<ItemHistorialResponse>>>(relaxed = true)
        val slotCallback = slot<Callback<List<ItemHistorialResponse>>>()


        every { mockApiService.obtenerPedidosPorUsuario(any()) } returns mockCall

        every { mockCall.enqueue(capture(slotCallback)) } answers {
            // Simulamos éxito
            slotCallback.captured.onResponse(mockCall, Response.success(listaRespuesta))
        }

        //Ejecución
        presenter.cargarHistorialDeUsuario(123)

        //Verificación
        verify { view.mostrarCarga() }
        verify { view.ocultarCarga() }

        // Verificamos que llegó al final y llamó a mostrarLista
        verify { view.mostrarListaHistorial(any()) }
    }
    //falla al cargar la lista y muestra un error
    @Test
    fun errorAlCargar() {
        //Preparación: Simulamos que SharedPreferences devuelve -1
        every { mockSharedPrefs.getInt("user_id", -1) } returns -1

        // Creamos un carrito falso
        val productoMock = tblProductos(
            idProducto = 1, nombre = "Test", precio = 10.0,
            descripcion = "", imagenProdc = "" // Llenar según pida tu clase
        )
        val carrito = listOf(ItemCarrito(productoMock, 1))

        //Ejecución
        presenter.realizarPedido(carrito)

        //Verificación
        verify { view.mostrarError(match { it.contains("inicia sesión") }) }

        verify(exactly = 0) { mockApiService.registrarPedido(any()) }
    }
    //simulacion del pedido exitoso
    @Test
    fun pedidoExitoso() {
        //Preparación: Simulamos usuario ID 50 logueado
        every { mockSharedPrefs.getInt("user_id", -1) } returns 50

        // Datos del carrito
        val productoMock = tblProductos(
            idProducto = 10, nombre = "Capuchino", precio = 45.0,
            descripcion = "", imagenProdc = ""
        )
        val carrito = listOf(ItemCarrito(productoMock, 2))


        val respuestaServidor = clsDatosRespuestaH(
            error = false,
            mensaje = "Pedido registrado con éxito"

        )

        // Mock de Retrofit
        val mockCall = mockk<Call<clsDatosRespuestaH>>(relaxed = true)
        val slotCallback = slot<Callback<clsDatosRespuestaH>>()

        every { mockApiService.registrarPedido(any()) } returns mockCall

        every { mockCall.enqueue(capture(slotCallback)) } answers {
            slotCallback.captured.onResponse(mockCall, Response.success(respuestaServidor))
        }

        // 2. Ejecución
        presenter.realizarPedido(carrito)

        // 3. Verificación
        verify { view.mostrarExito("Pedido registrado con éxito") }

        // Opcional: Verificar que se envió el ID 50 al servidor
        val slotPedido = slot<PedidoRe>()
        verify { mockApiService.registrarPedido(capture(slotPedido)) }
        assert(slotPedido.captured.idUsuario == 50)
    }
}