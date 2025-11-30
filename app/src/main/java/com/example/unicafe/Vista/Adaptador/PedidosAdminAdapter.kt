package com.example.unicafe.Vista.Adaptador
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unicafe.Modelo.clsUsuarioPedido
import com.example.unicafe.R

class PedidosAdminAdapter(
    private val listaUsuarios: List<clsUsuarioPedido>,
    private val onVerClickListener: (clsUsuarioPedido) -> Unit
) : RecyclerView.Adapter<PedidosAdminAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvAdminNombreUser)
        val tvTelefono: TextView = view.findViewById(R.id.tvAdminTelefono)
        val tvTotal: TextView = view.findViewById(R.id.tvAdminTotal)
        val btnVer: Button = view.findViewById(R.id.btnAdminVerPedidos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.usuariopedido, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaUsuarios[position]
        holder.tvNombre.text = item.nombreUsuario
        holder.tvTelefono.text = "Tel: ${item.telefono}"
        holder.tvTotal.text = "Total: $${item.totalAcumulado} (${item.cantidadPedidos} pedidos)"

        holder.btnVer.setOnClickListener {
            onVerClickListener(item)
        }
    }

    override fun getItemCount() = listaUsuarios.size
}