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
import com.example.unicafe.Presentador.HistorialPresenter
import com.example.unicafe.R
import com.example.unicafe.Vista.Adaptador.HistorialAdapter
import com.example.unicafe.Vista.Contract.HistorialContract

class Historial : AppCompatActivity(), HistorialContract.View {

    private lateinit var rcvHistorial: RecyclerView
    private lateinit var tvTotalPedido: TextView
    private lateinit var btnRealizarPedido: Button
    // Agrega un ProgressBar en tu XML activity_historial.xml para mostrar carga
    private lateinit var progressBar: ProgressBar
    private lateinit var adaptador: HistorialAdapter
    private lateinit var presenter: HistorialPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        // 1. Inicializar Presenter
        presenter = HistorialPresenter(this, this)

        // 2. Enlazar vistas
        rcvHistorial = findViewById(R.id.rcvHistorial)
        tvTotalPedido = findViewById(R.id.txtTotal)
        btnRealizarPedido = findViewById(R.id.btnRealizarPedido)
        // Asegúrate de agregar esto en tu XML y ponerle este ID
        progressBar = findViewById(R.id.pgbCarga)

        // 3. Configurar RecyclerView
        rcvHistorial.layoutManager = LinearLayoutManager(this)
        adaptador = HistorialAdapter(this, CarritoManager.itemsCarrito)
        rcvHistorial.adapter = adaptador

        actualizarUI()

        // 4. Acción del botón
        btnRealizarPedido.setOnClickListener {
            if (CarritoManager.itemsCarrito.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            } else {
                // Llamamos al presentador con la lista actual del singleton
                presenter.realizarPedido(CarritoManager.itemsCarrito)
            }
        }
    }

    private fun actualizarUI() {
        tvTotalPedido.text = "Total: $${CarritoManager.obtenerTotalPedido()}"
        adaptador.notifyDataSetChanged()
    }

    // --- Implementación del Contrato MVP ---

    override fun mostrarCarga() {
        progressBar.visibility = View.VISIBLE
        btnRealizarPedido.isEnabled = false
        btnRealizarPedido.text = "Enviando..."
    }

    override fun ocultarCarga() {
        progressBar.visibility = View.GONE
        btnRealizarPedido.isEnabled = true
        btnRealizarPedido.text = "Realizar Pedido"
    }

    override fun mostrarExito(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        // Éxito: Limpiamos el carrito local
        CarritoManager.limpiarCarrito()
        // Actualizamos la pantalla (quedará vacía y total 0)
        actualizarUI()
        // Opcional: cerrar la actividad automáticamente después de unos segundos
        /*
        rcvHistorial.postDelayed({
            finish()
        }, 2000)
        */
    }

    override fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        // Asegura que si volvemos a la pantalla, se refresque la lista
        if(::adaptador.isInitialized) {
            actualizarUI()
        }
    }
}