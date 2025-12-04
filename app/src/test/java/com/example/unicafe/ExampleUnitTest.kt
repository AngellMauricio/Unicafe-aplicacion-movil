package com.example.unicafe

import com.example.unicafe.Modelo.LoginModel
import com.example.unicafe.Presentador.LoginPresenter
import com.example.unicafe.Vista.Contract.LoginContrac
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


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

    @MockK(relaxed = true)
    private lateinit var view: LoginContrac

    // CAMBIO 1: El modelo NO es un Mock, es la clase real
    private lateinit var model: LoginModel

    private lateinit var presenter: LoginPresenter

    @Before
    fun setUp() {
        MockKAnnotations.init(this)


        model = LoginModel()

        presenter = LoginPresenter(view, model)
    }
//prueba que verifica el iniicio de sesion de un usuario como cliente
    @Test
    fun IniciarSesion() {
        // datos de un usuario que si existe
        val emailReal = "mauricio@gmail.com"
        val passReal = "123456"

        val lock = CountDownLatch(1)


        every { view.navegarACliente() } answers {
            println("Login exitoso detectado en la vista")
            lock.countDown()
        }
        //si el usuario es administrador
        every { view.navegarAAdmin() } answers {
            println("Login admin detectado")
            lock.countDown()
        }
        // Si falla la conexion o las credenciales son incorrectas

        every { view.mostrarMensaje(any<String>()) } answers {
            println("Error recibido: ${firstArg<String>()}")
            lock.countDown()
        }

        println("Iniciando petición al servidor...")
        presenter.iniciarSesion(emailReal, passReal)

        val resultado = lock.await(10, TimeUnit.SECONDS)

        if (!resultado) {
            throw RuntimeException("El servidor tardó demasiado en responder (Time out)")
        }
        verify { view.navegarACliente() }
        verify(exactly = 0) { view.mostrarMensaje(any()) }
    }
    //Test que prueba que una credencial sea incorrecta
    @Test
    fun CredencialesIncorrectas() {
        val emailFake = "error@gmail.com"
        val passFake = "123456"
        val lock = CountDownLatch(1)


        var loginFueExitosoIncorrectamente = false

        //Cuando las credenciales son correctas
        every { view.navegarACliente() } answers {
            println("ERROR: El servidor aceptó las credenciales e intentó navegar.")
            loginFueExitosoIncorrectamente = true
            lock.countDown()
        }

    //Cuando las credenciales son correctas de un admin
        every { view.navegarAAdmin() } answers {
            println("ERROR: El servidor aceptó las credenciales como ADMIN.")
            loginFueExitosoIncorrectamente = true
            lock.countDown()
        }

        //cuando la prueba rechada las credenciales correctamente
        every { view.mostrarMensaje(any<String>()) } answers {
            println("Comportamiento correcto - Mensaje recibido: ${firstArg<String>()}")
            lock.countDown()
        }


        presenter.iniciarSesion(emailFake, passFake)


        val llegoRespuesta = lock.await(10, TimeUnit.SECONDS)


        if (!llegoRespuesta) {
            throw RuntimeException("El servidor tardó demasiado y no respondió nada.")
        }


        if (loginFueExitosoIncorrectamente) {

            throw AssertionError("El test falló porque las credenciales '$emailFake' fueron aceptadas por el servidor real, pero esperábamos un error.")
        }


        verify { view.mostrarMensaje(or("Credenciales incorrectas", "Usuario no encontrado")) }


        verify(exactly = 0) { view.navegarACliente() }
    }
    // Test que prueba una "inyecion de sql" en el servidor

    @Test
    fun PruebaInyecionSQL() {

        val emailHack = "' OR '1'='1"
        val passHack = "admin' --"
        val lock = CountDownLatch(1)

        every { view.mostrarMensaje(any()) } answers {
            println("El servidor manejó correctamente el intento de hackeo: ${firstArg<String>()}")
            lock.countDown()
        }

        // Si entra a navegar, ¡tienes un problema de seguridad GRAVE en el servidor!
        every { view.navegarACliente() } answers { lock.countDown() }
        every { view.navegarAAdmin() } answers { lock.countDown() }

        presenter.iniciarSesion(emailHack, passHack)
        lock.await(10, TimeUnit.SECONDS)

        // Esperamos que el servidor rechace el login
        verify { view.mostrarMensaje(any()) }
        verify(exactly = 0) { view.navegarACliente() }
    }
}