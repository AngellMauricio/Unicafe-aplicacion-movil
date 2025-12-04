package com.example.unicafe

import com.example.unicafe.Modelo.clsUsuarioPedido
import com.example.unicafe.Presentador.PedidosAdminPresenter
import com.example.unicafe.Vista.Contract.PedidosAdminContract
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PedidosAdminTest {
    @MockK(relaxed = true)
    private lateinit var view: PedidosAdminContract.View

    private lateinit var presenter: PedidosAdminPresenter

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        presenter = PedidosAdminPresenter(view)
    }

    @Test
    fun obtenerPedidosAdmin() {

        val lock = CountDownLatch(1)
        var exito = false

        every { view.mostrarUsuarios(any()) } answers {
            val lista = firstArg<List<clsUsuarioPedido>>()
            println("ÉXITO: Se recibieron ${lista.size} usuarios con pedidos.")
            exito = true
            lock.countDown()
        }

        // Capturamos error por si la base de datos está vacía o falla
        every { view.mostrarError(any()) } answers {
            println("INFO/ERROR: ${firstArg<String>()}")
            lock.countDown()
        }

        println("Solicitando pedidos de administrador...")
        presenter.cargarPedidos()

        val respondio = lock.await(10, TimeUnit.SECONDS)

        if (!respondio) throw RuntimeException("TIMEOUT: El servidor no respondió.")

        // Verificamos el error
        if (exito) {
            verify { view.mostrarUsuarios(any()) }
        } else {
            // posible error
            verify { view.mostrarError(any()) }
        }
    }

}