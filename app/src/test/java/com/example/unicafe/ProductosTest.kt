package com.example.unicafe

import com.example.unicafe.Modelo.tblProductos
import com.example.unicafe.Presentador.ProductosPresenter
import com.example.unicafe.Vista.Contract.ProductosContract
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ProductosTest {
    @MockK(relaxed = true)
    private lateinit var view: ProductosContract

    private lateinit var presenter: ProductosPresenter

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        // Se conecta a la URL definida dentro de ProductosPresenter ("https://unicafe.grupoctic.com...")
        presenter = ProductosPresenter(view)
    }


    // TEST: OBTENER PRODUCTOS (Menú)
    @Test
    fun obtenerProductos() {

        val lock = CountDownLatch(1)
        var exito = false

        //El servidor devuelve la lista
        every { view.mostrarProductos(any()) } answers {
            val lista = firstArg<List<tblProductos>>()
            println("ÉXITO: Se recibieron ${lista.size} productos del menú.")
            exito = true
            lock.countDown()
        }

        //Si falla la conexión o el servidor
        every { view.mostrarError(any()) } answers {
            println("ERROR DEL SERVIDOR: ${firstArg<String>()}")
            lock.countDown()
        }

        println("Solicitando lista de productos...")
        presenter.obtenerProductos()

        // Esperamos respuesta
        val respondio = lock.await(10, TimeUnit.SECONDS)

        if (!respondio) throw RuntimeException("⏱ TIMEOUT: El servidor no respondió.")


        if (!exito) {
            throw AssertionError("El test falló: Se esperaba la lista de productos pero ocurrió un error o no llegaron datos.")
        }

        verify { view.mostrarProductos(any()) }
    }
}