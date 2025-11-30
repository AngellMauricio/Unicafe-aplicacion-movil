package com.example.unicafe.Vista

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unicafe.Modelo.CarritoManager
import com.example.unicafe.Modelo.ItemCarrito
import com.example.unicafe.Presentador.HistorialPresenter
import com.example.unicafe.R
import com.example.unicafe.Vista.Adaptador.HistorialAdapter
import com.example.unicafe.Vista.Contract.HistorialContract

class Historial : AppCompatActivity(), HistorialContract.View {

    private lateinit var rcvHistorial: RecyclerView
    private lateinit var tvTotalPedido: TextView
    private lateinit var btnRealizarPedido: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var adaptador: HistorialAdapter
    private lateinit var presenter: HistorialPresenter
    private var esModoAdmin = false
    private var idClienteVer = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        // Inicializar vistas
        rcvHistorial = findViewById(R.id.rcvHistorial)
        tvTotalPedido = findViewById(R.id.txtTotal)
        btnRealizarPedido = findViewById(R.id.btnRealizarPedido)
        progressBar = findViewById(R.id.pgbCarga)

        presenter = HistorialPresenter(this, this)
        rcvHistorial.layoutManager = LinearLayoutManager(this)


        // 1. VERIFICAR MODO
        esModoAdmin = intent.getBooleanExtra("MODO_ADMIN", false)
        idClienteVer = intent.getIntExtra("ID_CLIENTE_A_VER", -1)

        if (esModoAdmin && idClienteVer != -1) {
            // --- MODO ADMIN (SOLO LECTURA) ---
            btnRealizarPedido.visibility = View.GONE // Ocultar botón de comprar
            tvTotalPedido.text = "Cargando historial del cliente..."

            // Pedir datos al servidor
            presenter.cargarHistorialDeUsuario(idClienteVer)

        } else {
            // --- MODO CLIENTE (CARRITO) ---
            // Mostrar datos locales del carrito
            mostrarListaHistorial(CarritoManager.itemsCarrito)

            btnRealizarPedido.setOnClickListener {
                if (CarritoManager.itemsCarrito.isEmpty()) {
                    Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                } else {
                    presenter.realizarPedido(CarritoManager.itemsCarrito)
                }
            }
        }
    }

    private fun actualizarUI() {
        tvTotalPedido.text = "Total: $${CarritoManager.obtenerTotalPedido()}"
        adaptador.notifyDataSetChanged()
    }


    // Esta función sirve tanto para mostrar el carrito local como el historial remoto
    override fun mostrarListaHistorial(lista: List<ItemCarrito>) {
        adaptador = HistorialAdapter(this, lista)
        rcvHistorial.adapter = adaptador

        // Calcular y mostrar total
        var total = 0.0
        for (item in lista) {
            total += item.subtotal
        }

        if (esModoAdmin) {
            tvTotalPedido.text = "Total a Pagar: $$total"
        } else {
            tvTotalPedido.text = "Total: $$total"
        }
    }

    override fun mostrarCarga() {
        progressBar.visibility = View.VISIBLE
        btnRealizarPedido.isEnabled = false
        if (!esModoAdmin) btnRealizarPedido.text = "Enviando..."
    }

    override fun ocultarCarga() {
        progressBar.visibility = View.GONE
        btnRealizarPedido.isEnabled = true
        if (!esModoAdmin) btnRealizarPedido.text = "Realizar Pedido"
    }

    override fun mostrarExito(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        // Solo limpiamos si es una compra real, no si estamos viendo historial
        if (!esModoAdmin) {
            CarritoManager.limpiarCarrito()
            mostrarListaHistorial(CarritoManager.itemsCarrito)
        }
    }

    override fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        // Solo refrescamos el carrito local automáticamente
        if (!esModoAdmin && ::adaptador.isInitialized) {
            mostrarListaHistorial(CarritoManager.itemsCarrito)
        }
    }
}