package com.example.unicafe
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.unicafe.Vista.Productos
import com.example.unicafe.Vista.login
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val sharedPref = getSharedPreferences("MiAppPreferenciasGlobales", Context.MODE_PRIVATE)
        val rolId = sharedPref.getInt("rol_id", -1)
        if (rolId == 1 || rolId == 2) {
            Log.d(TAG, "Usuario Admin/Empleado detectado. Notificación suprimida.")
            return
        }

        if (remoteMessage.data.isNotEmpty()) {
            val data = remoteMessage.data
            val titulo = data["titulo"] ?: "Novedad Unicafe"
            val mensaje = data["mensaje"] ?: "Nuevo producto disponible"

            val idProducto = data["idProducto"]
            val nombre = data["nombre"]

            if (idProducto != null && nombre != null) {
                mostrarNotificacion(titulo, mensaje, data)
            }
        }
    }

    private fun mostrarNotificacion(titulo: String, mensaje: String, data: Map<String, String>) {
        val intent = Intent(this, login::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        intent.putExtra("abrir_detalle", true)
        intent.putExtra("idProducto", data["idProducto"])
        intent.putExtra("nombre", data["nombre"])
        intent.putExtra("precio", data["precio"])
        intent.putExtra("descripcion", data["descripcion"])
        intent.putExtra("imagenProdc", data["imagenProdc"])

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val channelId = "unicafe_productos"
        val sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setAutoCancel(true)
            .setSound(sonido)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Productos Unicafe", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}