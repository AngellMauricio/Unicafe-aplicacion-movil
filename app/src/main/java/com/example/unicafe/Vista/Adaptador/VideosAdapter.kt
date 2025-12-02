package com.example.unicafe.Vista.Adaptador

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import com.example.unicafe.Modelo.tblVideos
import com.example.unicafe.R

class VideosAdapter (private val context: Context, private val listaVideos: List<tblVideos>) :
    RecyclerView.Adapter<VideosAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playerView: PlayerView = view.findViewById(R.id.playerView)
        var player: ExoPlayer? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Usamos tu layout cardvideo.xml
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardvideo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = listaVideos[position]

        // Configurar ExoPlayer
        if (holder.player == null) {
            holder.player = ExoPlayer.Builder(context).build()
            holder.playerView.player = holder.player
        }

        val mediaItem = MediaItem.fromUri(Uri.parse(video.urlVideo))
        holder.player?.setMediaItem(mediaItem)
        holder.player?.prepare()
        // holder.player?.play() // Descomentar si quieres autoplay (no recomendado en listas)
    }

    // Importante: Liberar recursos cuando la vista se recicla para no saturar memoria
    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.player?.release()
        holder.player = null
    }

    override fun getItemCount(): Int = listaVideos.size
}