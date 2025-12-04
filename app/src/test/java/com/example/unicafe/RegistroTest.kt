package com.example.unicafe


import com.example.unicafe.Modelo.ifaceApiService
import com.example.unicafe.Modelo.RegistroModel
import com.example.unicafe.Presentador.RegistrPresenter
import com.example.unicafe.Vista.Contract.RegistroContrac
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class RegistroTest {

    @MockK(relaxed = true)
    private lateinit var vista: RegistroContrac


    private lateinit var model: RegistroModel

    private lateinit var presenter: RegistrPresenter

    @Before
    fun setUp() {
        MockKAnnotations.init(this)


        val retrofit = Retrofit.Builder()
            .baseUrl("https://unicafe.grupoctic.com/appMovil/api/") // <--- CAMBIA ESTO POR TU IP REAL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiReal = retrofit.create(ifaceApiService::class.java)


        model = RegistroModel(apiReal)

        presenter = RegistrPresenter(vista, model)
    }

    //verifica un registro exito o falla si ya hay un correo con ese registro
    @Test
    fun RegistrarUsuario() {
        //val emailNuevo = "nuevo_${System.currentTimeMillis()}@gmail.com"
        val emailNuevo = "yo@gmail.com"
        val pass = "12345"

        val lock = CountDownLatch(1)


        every { vista.registroExitoso() } answers {
            println("ÉXITO: Se llamó a registroExitoso")
            lock.countDown()
        }

        every { vista.mostrarMensaje(any()) } answers {
            println("MENSAJE: Se llamó a mostrarMensaje: ${firstArg<String>()}")

        }

        println("Probando registro exitoso con: $emailNuevo")
        presenter.registrarUsuario("Test", emailNuevo, pass, "7712345678")

        val ok = lock.await(10, TimeUnit.SECONDS)
        if (!ok) throw RuntimeException("Timeout esperando respuesta del servidor")


        verify { vista.registroExitoso() }


        verify { vista.mostrarMensaje(any()) }
    }

    //unicamete verifica que no se pueda registrar con un correo ya registrado
    @Test
    fun RegistrarEmailDuplicado() {
        val emailExistente = "jk@gmail.com"
        val pass = "12345"

        val lock = CountDownLatch(1)
        var comportamientoIncorrecto = false

        every { vista.registroExitoso() } answers {
            println("ERROR: El servidor aceptó registrar un correo duplicado.")
            comportamientoIncorrecto = true
            lock.countDown()
        }

        every { vista.mostrarMensaje(any()) } answers {
            println("Comportamiento correcto - Mensaje recibido: ${firstArg<String>()}")
            lock.countDown()
        }

        println("Iniciando registro con email existente: $emailExistente")
        presenter.registrarUsuario("MauroTest", emailExistente, pass, "7710000000")

        val llegoRespuesta = lock.await(10, TimeUnit.SECONDS)

        // Validaciones idénticas a tu test de Login
        if (!llegoRespuesta) {
            throw RuntimeException("El servidor tardó demasiado y no respondió nada.")
        }

        if (comportamientoIncorrecto) {
            throw AssertionError("El test falló porque el servidor aceptó registrar '$emailExistente' de nuevo.")
        }

        verify(exactly = 0) { vista.registroExitoso() }
        verify { vista.mostrarMensaje(any()) }
    }
}