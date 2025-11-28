package com.example.unicafe.Vista

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unicafe.Modelo.CarritoManager
import com.example.unicafe.R
import com.example.unicafe.Vista.Adaptador.HistorialAdapter

class Historial : AppCompatActivity() {

    private lateinit var rcvHistorial: RecyclerView
    private lateinit var tvTotalPedido: TextView
    private lateinit var btnRealizarPedido: Button
    private lateinit var adaptador: HistorialAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial) // Asegúrate que este es tu layout de la imagen 4

        // 1. Enlazar vistas
        rcvHistorial = findViewById(R.id.rcvHistorial) // Pon este ID a tu RecyclerView en el XML
        tvTotalPedido = findViewById(R.id.txtTotal) // Pon este ID al TextView que dice "Total:"
        btnRealizarPedido = findViewById(R.id.btnRealizarPedido) // Pon este ID al botón café

        // 2. Configurar RecyclerView
        rcvHistorial.layoutManager = LinearLayoutManager(this)
        // Usamos la lista directamente del Singleton CarritoManager
        adaptador = HistorialAdapter(this, CarritoManager.itemsCarrito)
        rcvHistorial.adapter = adaptador

        // 3. Actualizar el total en pantalla
        actualizarTotalUI()

        // 4. Acción del botón Realizar Pedido
        btnRealizarPedido.setOnClickListener {
            if (CarritoManager.itemsCarrito.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            } else {
                realizarPedidoEnServidor()
            }
        }
    }

    private fun actualizarTotalUI() {
        val total = CarritoManager.obtenerTotalPedido()
        tvTotalPedido.text = "Total: $$total"
    }

    // Aquí irá la lógica para conectar con el Presenter y Retrofit en el futuro
    private fun realizarPedidoEnServidor() {
        // TODO: Implementar la llamada al Presenter para enviar la lista CarritoManager.itemsCarrito
        Toast.makeText(this, "Procesando pedido... (Simulación)", Toast.LENGTH_LONG).show()

        // SIMULACIÓN DE ÉXITO:
        // 1. Limpiamos el carrito
        CarritoManager.limpiarCarrito()
        // 2. Notificamos al adaptador que los datos cambiaron
        adaptador.notifyDataSetChanged()
        // 3. Actualizamos el total a 0
        actualizarTotalUI()
        Toast.makeText(this, "¡Pedido realizado con éxito!", Toast.LENGTH_SHORT).show()
        // Opcional: finish() para cerrar la pantalla de historial
    }

    // Opcional: Si regresas a esta pantalla, actualizar los datos
    override fun onResume() {
        super.onResume()
        if(::adaptador.isInitialized) {
            adaptador.notifyDataSetChanged()
            actualizarTotalUI()
        }
    }
}