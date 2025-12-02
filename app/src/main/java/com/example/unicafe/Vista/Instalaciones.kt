package com.example.unicafe.Vista

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unicafe.Modelo.tblVideos
import com.example.unicafe.Presentador.InstalacionesPresenter
import com.example.unicafe.R
import com.example.unicafe.Vista.Adaptador.VideosAdapter
import com.example.unicafe.Vista.Contract.InstalacionesContract

class Instalaciones : AppCompatActivity() , InstalacionesContract {

    private lateinit var rcvInstalaciones: RecyclerView
    private lateinit var presenter: InstalacionesPresenter

    private var adapter: VideosAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_instalaciones)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rcvInstalaciones = findViewById(R.id.rcvInstalaciones)
        rcvInstalaciones.layoutManager = LinearLayoutManager(this)

        presenter = InstalacionesPresenter(this)
        presenter.obtenerVideos()
    }

    override fun mostrarVideos(videos: List<tblVideos>) {
        adapter = VideosAdapter(this, videos)
        rcvInstalaciones.adapter = adapter
    }

    override fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        adapter?.liberarReproductores()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter?.liberarReproductores()
    }
}