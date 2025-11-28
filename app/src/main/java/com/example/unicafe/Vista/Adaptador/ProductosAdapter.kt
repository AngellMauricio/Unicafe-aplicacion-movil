package com.example.unicafe.Vista.Adaptador

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.unicafe.Modelo.CarritoManager
import com.example.unicafe.Modelo.tblProductos
import com.example.unicafe.R
import com.example.unicafe.Vista.Historial
import com.example.unicafe.Vista.detalleProducto

class ProductosAdapter(val contexto: Context, val listaProductos: List<tblProductos>): RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder>() {
    class ProductoViewHolder(control: View): RecyclerView.ViewHolder(control){
        val imgProducto : ImageView = control.findViewById(R.id.imgProducto)
        val txtNombrePlatillo : TextView = control.findViewById(R.id.txtNombrePlatillo)
        val txtPrecioPlatillo : TextView = control.findViewById(R.id.txtPrecioPlatillo)
        val btnOrdena : Button = control.findViewById(R.id.btnOrdena)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.platillos, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = listaProductos[position]
        holder.txtNombrePlatillo.text = producto.nombre
        holder.txtPrecioPlatillo.text = "$" + producto.precio.toString()

        Glide.with(contexto)
            .load("https://unicafe.grupoctic.com/" + producto.imagenProdc)
            .into(holder.imgProducto)
        holder.imgProducto.setOnClickListener {
            verDetallePopup(producto)
        }

        holder.btnOrdena.setOnClickListener {
            // Agregamos al singleton
            CarritoManager.agregarProducto(producto)
            // Feedback visual
            Toast.makeText(contexto, "${producto.nombre} agregado al pedido", Toast.LENGTH_SHORT).show()
            mostrarHistorial(producto)
        }
    }

    private fun verDetallePopup(producto : tblProductos)
    {
        val activity = contexto as? AppCompatActivity
        if (activity != null) {
            val dialog = detalleProducto.newInstance(producto)
            dialog.show(activity.supportFragmentManager, "DetalleDialog")
        } else {
            Toast.makeText(contexto, "Error al abrir detalle", Toast.LENGTH_SHORT).show()
        }
    }

    fun mostrarHistorial(producto : tblProductos)
    {
        val intent = Intent(contexto, Historial::class.java)

        contexto.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return listaProductos.size
    }
}