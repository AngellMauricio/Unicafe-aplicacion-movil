package com.example.unicafe.Vista.Adaptador

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
            verDetalle(producto)
        }
        holder.btnOrdena.setOnClickListener {
            mostrarHistorial(producto)
        }
    }

    fun verDetalle(producto : tblProductos)
    {
        val intent = Intent(contexto, detalleProducto::class.java).apply {
            putExtra("producto_id", producto.idProducto)
            putExtra("nombre_producto", producto.nombre)
            putExtra("descripcion_producto", producto.descripcion)
            putExtra("precio_producto", producto.precio)
            putExtra("imagen_producto", producto.imagenProdc)
        }
        contexto.startActivity(intent)
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