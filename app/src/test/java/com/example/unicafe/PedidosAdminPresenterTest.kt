package com.example.unicafe

import com.example.unicafe.Modelo.clsUsuarioPedido
import com.example.unicafe.Modelo.ifaceApiService
import com.example.unicafe.Presentador.PedidosAdminPresenter
import com.example.unicafe.Vista.Contract.PedidosAdminContract
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


class PedidosAdminPresenterTest {
    // 1. Mocks necesarios
    @MockK(relaxed = true)
    private lateinit var view: PedidosAdminContract.View

    @MockK
    private lateinit var mockApiService: ifaceApiService

    @MockK(relaxed = true)
    private lateinit var mockCall: Call<List<clsUsuarioPedido>>

    // El presenter real
    private lateinit var presenter: PedidosAdminPresenter

    @Before
    fun setUp() {
        MockKAnnotations.init(this)


        presenter = PedidosAdminPresenter(view)

        try {
            //Obtenemos acceso al campo privado "apiService"
            val campoPrivado: Field = PedidosAdminPresenter::class.java.getDeclaredField("apiService")

            // Quitamos la protección de "private"
            campoPrivado.isAccessible = true

            // Reemplazamos el objeto real por nuestro Mock
            campoPrivado.set(presenter, mockApiService)

        } catch (e: Exception) {
            println("Error al hacer reflexión: ${e.message}")
        }

        //Configuración base: Que el servicio devuelva nuestra llamada falsa
        every { mockApiService.obtenerUsuariosConPedidos() } returns mockCall
    }
    //cargar pedidos con una conexion exitosa
    @Test
    fun conexionExitosa() {
        val usuarioPedidoMock = clsUsuarioPedido(

            idUsuario = 10,
            nombreUsuario = "juan",
            telefono = "7714058466",
            totalAcumulado = 525.00,
            cantidadPedidos = 2
        )
        val listaRespuesta = listOf(usuarioPedidoMock)

        val slotCallback = slot<Callback<List<clsUsuarioPedido>>>()

        every { mockCall.enqueue(capture(slotCallback)) } answers {
            val response = Response.success(listaRespuesta)
            slotCallback.captured.onResponse(mockCall, response)
        }

        presenter.cargarPedidos()

        verify { view.mostrarUsuarios(listaRespuesta) }
        verify(exactly = 0) { view.mostrarError(any()) }
    }
    //Probar un escenario con una conexion fallida
    @Test
    fun conexionFallida() {

        val slotCallback = slot<Callback<List<clsUsuarioPedido>>>()

        every { mockCall.enqueue(capture(slotCallback)) } answers {
            val errorBody = "Error".toResponseBody("text/plain".toMediaTypeOrNull())
            val response = Response.error<List<clsUsuarioPedido>>(500, errorBody)

            slotCallback.captured.onResponse(mockCall, response)
        }

        presenter.cargarPedidos()

        verify { view.mostrarError("No hay pedidos o error de servidor") }
    }
    //escenario que debe de mostrar un error en la conexion
    @Test
    fun errorConexion() {
        //Preparación
        val mensajeFallo = "No host found"
        val slotCallback = slot<Callback<List<clsUsuarioPedido>>>()

        every { mockCall.enqueue(capture(slotCallback)) } answers {
            // Simulamos que se cayó el internet (entra a onFailure)
            slotCallback.captured.onFailure(mockCall, Throwable(mensajeFallo))
        }

        //Ejecución
        presenter.cargarPedidos()

        //Verificación
        verify { view.mostrarError("Error de conexión: $mensajeFallo") }
    }
}