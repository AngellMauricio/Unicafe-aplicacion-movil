package com.example.unicafe

import com.example.unicafe.Modelo.LoginModel
import com.example.unicafe.Presentador.LoginPresenter
import com.example.unicafe.Vista.Contract.LoginContrac
import com.example.unicafe.Vista.clsDatosRespuesta
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    //Pruebas unitarias del login
    @MockK(relaxed = true)
    private lateinit var view: LoginContrac

    @MockK
    private lateinit var model: LoginModel

    // El presenter es el objeto real que vamos a probar
    private lateinit var presenter: LoginPresenter

    @Before
    fun setUp() {
        // Inicializa las anotaciones @MockK
        MockKAnnotations.init(this)
        // Inyectamos los mocks al presenter
        presenter = LoginPresenter(view, model)
    }
    //verifica el correcto funcionamiento de la validacion de campos vacios
    @Test
    fun camposvaciosLogin() {
        // Ejecución
        presenter.iniciarSesion("", "")

        // Verificación: Se debe llamar a mostrarMensaje con el texto específico
        verify { view.mostrarMensaje("Debe llenar todos los campos") }

        // Verificación: No se debe llamar al modelo si la validación falla
        verify(exactly = 0) { model.iniciarSesion(any(), any(), any()) }
    }
    //verifca el correcto funcionamiento de los roles
    @Test
    fun verificacionRoles() {
        // 1. Preparación de datos simulados
        val email = "cliente@test.com"
        val pass = "123456"
        val mockUser = clsDatosRespuesta(
            Estado = "Correcto",
            user_id = 10,
            rol_id = 3,
            Salida = "Exito"
        )
        val listaRespuesta = listOf(mockUser)

        // 2. Comportamiento del Mock del Modelo
        // Cuando llamen a model.iniciarSesion, capturamos el callback y respondemos exitosamente
        val slotCallback = slot<(List<clsDatosRespuesta>?, String?) -> Unit>()

        every {
            model.iniciarSesion(email, pass, capture(slotCallback))
        } answers {
            // Simulamos que el servidor respondió con la lista y sin error
            slotCallback.captured.invoke(listaRespuesta, null)
        }

        // 3. Ejecución
        presenter.iniciarSesion(email, pass)

        // 4. Verificación
        verify { view.guardarUsuarioSesion(10, 3) }
        verify { view.navegarACliente() }
        verify(exactly = 0) { view.mostrarMensaje(any()) } // No debe haber errores
    }
    @Test
    fun CredencialesIncorrectas() {
        // 1. Preparación (Estado NO es "Correcto" o lista nula)
        val email = "pedro@gmail.com"
        val pass = "wrong"

        // 2. Comportamiento Mock
        val slotCallback = slot<(List<clsDatosRespuesta>?, String?) -> Unit>()
        every {
            model.iniciarSesion(email, pass, capture(slotCallback))
        } answers {
            // Simulamos respuesta nula (login fallido lógico)
            slotCallback.captured.invoke(null, null)
        }

        // 3. Ejecución
        presenter.iniciarSesion(email, pass)

        // 4. Verificación
        verify { view.mostrarMensaje("Credenciales incorrectas") }
        verify(exactly = 0) { view.navegarACliente() }
        verify(exactly = 0) { view.navegarAAdmin() }
    }

    //simula un fallo tecnico o de servidor

    @Test
    fun errorDeConexion() {

        val email = "error@test.com"
        val pass = "123"
        val mensajeError = "Error de conexión con el servidor"

        val slotCallback = slot<(List<clsDatosRespuesta>?, String?) -> Unit>()
        every {
            model.iniciarSesion(email, pass, capture(slotCallback))
        } answers {
            slotCallback.captured.invoke(null, mensajeError)
        }

        presenter.iniciarSesion(email, pass)

        val listaMensajes = mutableListOf<String>()

        verify { view.mostrarMensaje(capture(listaMensajes)) }

        val mensajeRecibido = listaMensajes.first()
        println("Mensaje recibido: $mensajeRecibido")
    }
}