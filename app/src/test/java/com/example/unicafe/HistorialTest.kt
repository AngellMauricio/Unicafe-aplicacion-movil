package com.example.unicafe

import android.content.Context
import com.example.unicafe.Modelo.ItemCarrito
import com.example.unicafe.Presentador.HistorialPresenter
import com.example.unicafe.Vista.Contract.HistorialContract
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class HistorialTest {
    @MockK(relaxed = true)
    private lateinit var view: HistorialContract.View

    // Necesitamos mockear el Contexto porque tu Presentador lo pide
    @MockK(relaxed = true)
    private lateinit var context: Context

    private lateinit var presenter: HistorialPresenter

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        // Instanciamos el Presentador tal cual está en tu código.
        // NOTA: Se conectará a "https://unicafe.grupoctic.com/appMovil/api/"
        // porque eso está escrito dentro del 'init' del Presentador.
        presenter = HistorialPresenter(view, context)
    }

    // ================================================================
    //  TEST 1: CARGAR HISTORIAL (Usuario Existente)
    // ================================================================
    @Test
    fun cargarHistorialDeUsuario_Exito() {
        // ID de un usuario que exista en el servidor de 'grupoctic.com'
        val idUsuarioReal = 8

        val lock = CountDownLatch(1)
        var exito = false

        // 1. Esperamos recibir la lista
        every { view.mostrarListaHistorial(any()) } answers {
            val lista = firstArg<List<ItemCarrito>>()
            println("✅ ÉXITO: Historial recibido con ${lista.size} elementos.")
            exito = true
            lock.countDown()
        }

        // 2. Si da error, queremos ver qué pasó
        every { view.mostrarError(any()) } answers {
            println("❌ ERROR DEL SERVIDOR: ${firstArg<String>()}")
            lock.countDown()
        }

        println("Solicitando historial al servidor para ID: $idUsuarioReal")
        presenter.cargarHistorialDeUsuario(idUsuarioReal)

        val respondio = lock.await(10, TimeUnit.SECONDS)

        if (!respondio) {
            throw RuntimeException("El servidor no respondió. Revisa tu internet o la URL en HistorialPresenter.")
        }

        if (!exito) {
            throw AssertionError("El test falló: El servidor respondió con un error en lugar de la lista.")
        }

        verify { view.mostrarListaHistorial(any()) }
    }


    // TEST: error en un historia de un usuario que no existe o sin pedidos
    @Test
    fun cargarHistorialDeUsuarioNoEncontrado() {
        // ID que no existe (para asegurar lista vacía)
        val idUsuarioInvalido = 1

        val lock = CountDownLatch(1)

        // Variable para validar si la lógica fue correcta
        var pruebaPasoLogicamente = false

        every { view.mostrarCarga() } just Runs
        every { view.ocultarCarga() } just Runs

        //El servidor devuelve lista vacía
        every { view.mostrarListaHistorial(any()) } answers {
            val lista = firstArg<List<ItemCarrito>>()

            if (lista.isEmpty()) {
                println("CORRECTO: El servidor devolvió lista vacía.")
                pruebaPasoLogicamente = true
            } else {
                println("FALLO: El servidor devolvió datos.")
            }
            lock.countDown()
        }

        //El servidor devuelve error
        every { view.mostrarError(any()) } answers {
            println("CORRECTO: El sistema mostró error: ${firstArg<String>()}")
            pruebaPasoLogicamente = true
            lock.countDown()
        }

        println("Solicitando historial inválido ID: $idUsuarioInvalido")
        presenter.cargarHistorialDeUsuario(idUsuarioInvalido)

        val respondio = lock.await(10, TimeUnit.SECONDS)

        if (!respondio) throw RuntimeException("⏱ TIMEOUT: El servidor no respondió.")


        if (!pruebaPasoLogicamente) {
            throw AssertionError("El test falló: Se recibieron datos inesperados.")
        }

        verify { view.mostrarListaHistorial(any()) }
    }
}