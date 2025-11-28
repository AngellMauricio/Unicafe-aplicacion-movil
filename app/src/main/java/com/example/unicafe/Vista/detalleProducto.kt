package com.example.unicafe.Vista

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.unicafe.Modelo.CarritoManager
import com.example.unicafe.Modelo.tblProductos
import com.example.unicafe.R

class detalleProducto : DialogFragment() {

    private lateinit var productoActual: tblProductos

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.detalleproductos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getInt("producto_id") ?: 0
        val nombre = arguments?.getString("nombre_producto") ?: ""
        val desc = arguments?.getString("descripcion_producto") ?: ""
        val precio = arguments?.getDouble("precio_producto") ?: 0.0
        val imagenUrl = arguments?.getString("imagen_producto") ?: ""

        productoActual = tblProductos(id, nombre, desc, precio, imagenUrl)

        val imgDetalle: ImageView = view.findViewById(R.id.ivDetalleImagen)
        val tvNombre: TextView = view.findViewById(R.id.tvDetalleTitulo)
        val tvDesc: TextView = view.findViewById(R.id.tvDetalleDescripcion)
        val tvPrecio: TextView = view.findViewById(R.id.tvDetalleDescripcion)
        val btnAgregarCarrito: Button = view.findViewById(R.id.btnOrdenar)

        tvNombre.text = nombre
        tvDesc.text = desc
        tvPrecio.text = "$ $precio"

        Glide.with(requireContext())
            .load("https://unicafe.grupoctic.com/" + imagenUrl)
            .into(imgDetalle)

        btnAgregarCarrito.setOnClickListener {
            CarritoManager.agregarProducto(productoActual)
            Toast.makeText(requireContext(), "Agregado: $nombre", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    companion object {
        fun newInstance(producto: tblProductos): detalleProducto {
            val fragment = detalleProducto()
            val args = Bundle().apply {
                putInt("producto_id", producto.idProducto)
                putString("nombre_producto", producto.nombre)
                putString("descripcion_producto", producto.descripcion)
                putDouble("precio_producto", producto.precio)
                putString("imagen_producto", producto.imagenProdc)
            }
            fragment.arguments = args
            return fragment
        }
    }
}