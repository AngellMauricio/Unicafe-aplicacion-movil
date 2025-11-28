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

// Cambiamos de AppCompatActivity a DialogFragment
class detalleProducto : DialogFragment() {

    private lateinit var productoActual: tblProductos

    // Opcional: Para que el fondo sea transparente y se vea como un popup flotante
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
        // Inflamos el layout XML existente
        return inflater.inflate(R.layout.detalleproductos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Recuperar datos del Bundle
        val id = arguments?.getInt("producto_id") ?: 0
        val nombre = arguments?.getString("nombre_producto") ?: ""
        val desc = arguments?.getString("descripcion_producto") ?: ""
        val precio = arguments?.getDouble("precio_producto") ?: 0.0
        val imagenUrl = arguments?.getString("imagen_producto") ?: ""

        // Reconstruimos el objeto producto (necesario para agregarlo al carrito después)
        productoActual = tblProductos(id, nombre, desc, precio, imagenUrl)

        // 2. Encontrar vistas en el layout inflado
        val imgDetalle: ImageView = view.findViewById(R.id.ivDetalleImagen) // Asegúrate que este ID exista en tu XML
        val tvNombre: TextView = view.findViewById(R.id.tvDetalleTitulo) // Asegúrate que este ID exista
        val tvDesc: TextView = view.findViewById(R.id.tvDetalleDescripcion) // Asegúrate que este ID exista
        val tvPrecio: TextView = view.findViewById(R.id.tvDetalleDescripcion) // Asegúrate que este ID exista
        val btnAgregarCarrito: Button = view.findViewById(R.id.btnOrdenar) // ¡Necesitas este botón en tu XML!

        // 3. Setear los datos
        tvNombre.text = nombre
        tvDesc.text = desc
        tvPrecio.text = "$ $precio"

        Glide.with(requireContext())
            .load("https://unicafe.grupoctic.com/" + imagenUrl)
            .into(imgDetalle)

        // 4. Acción del botón "Agregar al carrito" dentro del popup
        btnAgregarCarrito.setOnClickListener {
            CarritoManager.agregarProducto(productoActual)
            Toast.makeText(requireContext(), "Agregado: $nombre", Toast.LENGTH_SHORT).show()
            dismiss() // Cierra el popup
        }
    }

    // Companion object para facilitar la creación de la instancia y el paso de argumentos
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