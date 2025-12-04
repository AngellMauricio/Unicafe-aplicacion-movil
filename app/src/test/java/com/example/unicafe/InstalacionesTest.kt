package com.example.unicafe

import com.example.unicafe.Modelo.tblVideos
import com.example.unicafe.Presentador.InstalacionesPresenter
import com.example.unicafe.Vista.Contract.InstalacionesContract
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class InstalacionesTest {
    @MockK(relaxed = true)
    private lateinit var view: InstalacionesContract

    private lateinit var presenter: InstalacionesPresenter

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        // Se conectará a la URL que está dentro de InstalacionesPresenter.kt
        presenter = InstalacionesPresenter(view)
    }


    // TEST: OBTENER VIDEOS
    @Test
    fun obtenerVideos_Exito() {
        val lock = CountDownLatch(1)
        var exito = false


        // El servidor responde con la lista de videos
        every { view.mostrarVideos(any()) } answers {
            val lista = firstArg<List<tblVideos>>()
            println("ÉXITO: Se recibieron ${lista.size} videos de instalaciones.")
            exito = true
            lock.countDown()
        }

        //error en el servidor
        every { view.mostrarError(any()) } answers {
            println("ERROR DEL SERVIDOR: ${firstArg<String>()}")
            lock.countDown()
        }

        println("Solicitando videos de instalaciones...")
        presenter.obtenerVideos()

        val respondio = lock.await(10, TimeUnit.SECONDS)

        if (!respondio) throw RuntimeException("⏱ TIMEOUT: El servidor no respondió.")

        // Validación: Debe haber sido exitoso
        if (!exito) {
            throw AssertionError("El test falló: El servidor respondió con error en lugar de mostrar los videos.")
        }

        verify { view.mostrarVideos(any()) }
    }
}