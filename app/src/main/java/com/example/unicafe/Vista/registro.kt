package com.example.unicafe.Vista

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.unicafe.Modelo.RegistroModel
import com.example.unicafe.Modelo.ifaceApiService
import com.example.unicafe.Presentador.RegistrPresenter
import com.example.unicafe.R
import com.example.unicafe.Vista.Contract.RegistroContrac
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.apply
import kotlin.jvm.java

class registro : AppCompatActivity(), RegistroContrac {
    private lateinit var etNombreUsuario: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPasswordRegistro: TextInputEditText
    private lateinit var etTelefono: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var presentador: RegistrPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        etNombreUsuario = findViewById(R.id.edtUNombre)
        etEmail = findViewById(R.id.edtCorreo)
        etTelefono = findViewById(R.id.edtTelefono)
        etPasswordRegistro = findViewById(R.id.etPassword)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        val gson = GsonBuilder().setLenient().create()
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val okHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://unicafe.grupoctic.com/appMovil/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val apiService = retrofit.create(ifaceApiService::class.java)
        val model = RegistroModel(apiService)
        presentador = RegistrPresenter(this, model)

        btnRegistrar.setOnClickListener {
            val nombreUsuario = etNombreUsuario.text.toString()
            val email = etEmail.text.toString()
            val password = etPasswordRegistro.text.toString()
            val telefono = etTelefono.text.toString()
            if (telefono.isEmpty()) {
                mostrarMensaje("Por favor ingrese su teléfono")
                return@setOnClickListener
            }
            presentador.registrarUsuario(nombreUsuario, email, password, telefono)
        }
    }

    override fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun registroExitoso() {
        finish()
    }
}