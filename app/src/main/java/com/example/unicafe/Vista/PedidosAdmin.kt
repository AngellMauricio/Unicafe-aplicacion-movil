package com.example.unicafe.Vista

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unicafe.Modelo.clsUsuarioPedido
import com.example.unicafe.Presentador.PedidosAdminPresenter
import com.example.unicafe.R
import com.example.unicafe.Vista.Adaptador.PedidosAdminAdapter
import com.example.unicafe.Vista.Contract.PedidosAdminContract

class PedidosAdmin : AppCompatActivity(), PedidosAdminContract.View {

    private lateinit var rcvPedidos: RecyclerView
    private lateinit var btnRefrescar: Button
    private lateinit var presenter: PedidosAdminPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        rcvPedidos = findViewById(R.id.rcvPedidosAdmin)

        btnRefrescar = findViewById(R.id.btnRefrescar)

        rcvPedidos.layoutManager = LinearLayoutManager(this)
        presenter = PedidosAdminPresenter(this)
        presenter.cargarPedidos()

        btnRefrescar.setOnClickListener {
            presenter.cargarPedidos()
        }
    }

    override fun mostrarUsuarios(lista: List<clsUsuarioPedido>) {
        android.util.Log.d("PedidosAdmin", "Recibidos ${lista.size} usuarios")

        val adapter = PedidosAdminAdapter(lista) { usuario ->
            navegarADetalleUsuario(usuario.idUsuario)
        }
        rcvPedidos.adapter = adapter
    }

    override fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun navegarADetalleUsuario(idUsuario: Int) {
        val intent = Intent(this, Historial::class.java)
        intent.putExtra("MODO_ADMIN", true)
        intent.putExtra("ID_CLIENTE_A_VER", idUsuario)
        startActivity(intent)
    }
}