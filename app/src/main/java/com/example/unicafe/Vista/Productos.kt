package com.example.unicafe.Vista

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        presenter = ProductosPresenter(this)
        presenter.obtenerProductos()

        btnInstalaciones.setOnClickListener {
            val intent = Intent(this, Instalaciones::class.java)
            startActivity(intent)
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