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
        verificarSesionYRedirigir()
        btnAcceder.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            presentador.iniciarSesion(email, password)
        }

        tvRegistrar.setOnClickListener {
            startActivity(Intent(this, registro::class.java))
        }
    }
    private fun verificarSesionYRedirigir() {
        val sharedPref = getSharedPreferences("MiAppPreferenciasGlobales", MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)
        val rolId = sharedPref.getInt("rol_id", -1)

        // Si hay sesión iniciada
        if (userId != -1) {
            if (rolId == 3) {
                // CASO CLIENTE
                val intentProductos = Intent(this, Productos::class.java)

                // Limpiar la pila para evitar bucles
                intentProductos.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                // --- CORRECCIÓN AQUÍ ---
                // Verificamos si el Intent que abrió este Login trae la orden de abrir detalle
                // Usamos 'this.intent' para asegurarnos de leer el intent de la Actividad
                if (this.intent.getBooleanExtra("abrir_detalle", false)) {
                    intentProductos.putExtras(this.intent) // Copiamos todos los datos (ID, nombre, etc.)
                }

                startActivity(intentProductos)
                finish()
            } else {
                // CASO ADMIN
                val intentAdmin = Intent(this, PedidosAdmin::class.java)
                intentAdmin.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intentAdmin)
                finish()
            }
        }
    }
    override fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun guardarUsuarioSesion(user_id: Int, rol_id: Int) {
        val sharedPref = getSharedPreferences("MiAppPreferenciasGlobales", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("user_id", user_id)
        editor.putInt("rol_id", rol_id)
        editor.apply()
    }

    override fun navegarACliente() {
        startActivity(Intent(this, Productos::class.java))
        finish()
    }

    override fun navegarAAdmin() {
        startActivity(Intent(this, PedidosAdmin::class.java))
        finish()
    }
}