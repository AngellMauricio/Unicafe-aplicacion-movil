package com.example.unicafe.Vista

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.unicafe.R
import com.example.unicafe.Presentador.LoginPresenter
import com.example.unicafe.Vista.Contract.LoginContrac
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.jvm.java

class login : AppCompatActivity(), LoginContrac {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnAcceder: Button
    private lateinit var tvRegistrar: TextView
    private lateinit var presentador: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        FirebaseMessaging.getInstance().subscribeToTopic("nuevos_productos")
            .addOnCompleteListener { task ->
                var msg = "Suscrito a notificaciones de productos"
                if (!task.isSuccessful) {
                    msg = "Fallo al suscribirse a notificaciones"
                }
                Log.d("FCM_SUSCRIPCION", msg)
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        etEmail = findViewById(R.id.edtUsuario)
        etPassword = findViewById(R.id.etPassword)
        btnAcceder = findViewById(R.id.btnAcceder)
        tvRegistrar = findViewById(R.id.txtRegistrar)

        presentador = LoginPresenter(this)

        btnAcceder.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            presentador.iniciarSesion(email, password)
        }

        tvRegistrar.setOnClickListener {
            startActivity(Intent(this, registro::class.java))
        }
    }

    override fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun navegarAMain() {

        startActivity(Intent(this, Productos::class.java))
        finish()
    }
    override fun guardarUsuarioSesion(user_id: Int) {
        // Usamos el nombre de archivo "MiAppPreferenciasGlobales"
        val sharedPref = getSharedPreferences("MiAppPreferenciasGlobales", MODE_PRIVATE)
        val editor = sharedPref.edit()
        // CLAVE ESTÁNDAR: "user_id"
        editor.putInt("user_id", user_id)
        editor.apply()
        Log.d("LOGIN", "ID de usuario guardado: $user_id")
    }
}