package com.example.unicafe.Presentador

import com.example.unicafe.Modelo.RegistroModel
import com.example.unicafe.Vista.Contract.RegistroContrac
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

class RegistrPresenterTest {
    @MockK(relaxed = true)
    private lateinit var view: RegistroContrac

    @MockK
    private lateinit var model: RegistroModel

    private lateinit var presenter: RegistrPresenter

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        presenter = RegistrPresenter(view, model)
    }

    @Test
    fun registroExitoso() {
        //Preparación de datos
        val nombre = "Mauro"
        val email = "mauro@test.com"
        val pass = "123456"
        val tel = "555555"
        val mensajeExito = "Usuario registrado correctamente"

        // Mock del Modelo (Capturamos la INTERFAZ)
        val slotListener = slot<RegistroModel.OnRegistroListener>()

        every {
            model.registrarUsuario(
                any(), // nombre
                any(), // email
                any(), // password
                any(), // telefono
                capture(slotListener) //Atrapamos el objeto listener aquí
            )
        } just Runs

        //Ejecución
        presenter.registrarUsuario(nombre, email, pass, tel)

        //Simulamos la respuesta del servidor
        slotListener.captured.onSuccess(mensajeExito)

        //Verificación
        verify { view.mostrarMensaje(mensajeExito) }
        verify { view.registroExitoso() }
    }

    @Test
    fun `registrarUsuario muestra error cuando falla el modelo`() {
        //reparación
        val nombre = "Mauro"
        val email = "existente@test.com"
        val pass = "123"
        val tel = "555"
        val mensajeError = "El correo ya está registrado"

        //Mock y Captura
        val slotListener = slot<RegistroModel.OnRegistroListener>()

        every {
            model.registrarUsuario(any(), any(), any(), any(), capture(slotListener))
        } just Runs

        //Ejecución
        presenter.registrarUsuario(nombre, email, pass, tel)

        //Simulamos el fallo
        slotListener.captured.onFailure(mensajeError)

        //Verificación
        verify { view.mostrarMensaje(mensajeError) }
        verify(exactly = 0) { view.registroExitoso() }
    }
}