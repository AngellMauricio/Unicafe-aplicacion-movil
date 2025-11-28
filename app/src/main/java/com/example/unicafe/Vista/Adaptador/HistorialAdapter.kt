package com.example.unicafe.Vista.Adaptador

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.unicafe.Modelo.ItemCarrito
import com.example.unicafe.R

class HistorialAdapter (val contexto: Context, val listaItems: List<ItemCarrito>) :
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    class HistorialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgCarritoProducto)
        val tvNombre: TextView = view.findViewById(R.id.tvCarritoNombre)
        val tvPrecioUni: TextView = view.findViewById(R.id.tvCarritoPrecioUnitario)
        val tvCantidad: TextView = view.findViewById(R.id.tvCarritoCantidad)
        val tvSubtotal: TextView = view.findViewById(R.id.tvCarritoSubtotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pedidorealizado, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val item = listaItems[position]

        holder.tvNombre.text = item.producto.nombre
        holder.tvPrecioUni.text = "Precio: $${item.producto.precio}"
        holder.tvCantidad.text = "Cantidad: ${item.cantidad}"
        holder.tvSubtotal.text = "Subtotal: $${item.subtotal}"

        Glide.with(contexto)
            .load("https://unicafe.grupoctic.com/" + item.producto.imagenProdc)
            .into(holder.img)
    }

    override fun getItemCount(): Int = listaItems.size
}