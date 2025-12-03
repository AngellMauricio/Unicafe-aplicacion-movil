package com.example.unicafe.Vista

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unicafe.Modelo.tblProductos
import com.example.unicafe.Presentador.ProductosPresenter
import com.example.unicafe.R
import com.example.unicafe.Vista.Adaptador.ProductosAdapter
import com.example.unicafe.Vista.Contract.ProductosContract

class Productos : AppCompatActivity(), ProductosContract {
    private lateinit var rcvProductos: RecyclerView
    private lateinit var presenter : ProductosPresenter
    private lateinit var btnInstalaciones: Button
    private lateinit var btnSalir : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = getSharedPreferences("MiAppPreferenciasGlobales", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)
        val rolId = sharedPref.getInt("rol_id", -1)

        if (userId == -1 || rolId != 3) {
            val intentLogin = Intent(this, login::class.java)
            startActivity(intentLogin)
            finish()
            return
        }
        enableEdgeToEdge()
        setContentView(R.layout.productos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rcvProductos = findViewById(R.id.rcvProductos)
        rcvProductos.layoutManager = LinearLayoutManager(this)
        btnInstalaciones = findViewById(R.id.btnInstalaciones)
        btnSalir = findViewById(R.id.btnSalir)

        presenter = ProductosPresenter(this)
        presenter.obtenerProductos()
        btnInstalaciones.setOnClickListener {
            val intent = Intent(this, Instalaciones::class.java)
            startActivity(intent)
        }
        btnSalir.setOnClickListener {
            cerrarSesion()
        }
        procesarNotificacion(intent)
    }

    fun cerrarSesion() {
        val editor = getSharedPreferences("MiAppPreferenciasGlobales", Context.MODE_PRIVATE).edit()
        editor.clear()
        editor.apply()

        val intent = Intent(this, login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        procesarNotificacion(intent)
    }
    private fun procesarNotificacion(intent: Intent?) {
        if (intent != null && intent.getBooleanExtra("abrir_detalle", false)) {

            val idStr = intent.getStringExtra("idProducto") ?: "0"
            val nombre = intent.getStringExtra("nombre") ?: ""
            val desc = intent.getStringExtra("descripcion") ?: ""
            val precioStr = intent.getStringExtra("precio") ?: "0.0"
            val imagen = intent.getStringExtra("imagenProdc")

            // Crear objeto producto manualmente con los datos de la notificación
            val productoNotificacion = tblProductos(
                idProducto = idStr.toIntOrNull() ?: 0,
                nombre = nombre,
                descripcion = desc,
                precio = precioStr.toDoubleOrNull() ?: 0.0,
                imagenProdc = imagen
            )

            // Abrir el detalle
            val dialog = detalleProducto.newInstance(productoNotificacion)
            dialog.show(supportFragmentManager, "DetalleDialog")

            // Limpiar el extra para que no se abra de nuevo al rotar pantalla
            intent.removeExtra("abrir_detalle")
        }
    }
    override fun mostrarProductos(productos: List<tblProductos>) {
        val adaptador = ProductosAdapter(this,productos)
        rcvProductos.adapter = adaptador
    }

    override fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}