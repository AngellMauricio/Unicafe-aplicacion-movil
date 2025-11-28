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
import com.example.unicafe.Vista.Productos // Importa tu actividad principal
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Esta función se ejecuta cuando llega una notificación y la app está en PRIMER PLANO
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Verificar si el mensaje contiene datos (payload)
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

        // Verificar si el mensaje contiene una notificación visual
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            // Si hay cuerpo de mensaje, lo mostramos
            it.body?.let { body -> sendNotification(it.title ?: "Unicafe", body) }
        }
    }

    // Esta función se ejecuta si Firebase asigna un nuevo token al dispositivo (poco común pero necesario)
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        // Aquí podrías enviar el token a tu servidor si lo necesitaras para mensajes individuales
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    // Función para construir y mostrar la notificación manualmente
    private fun sendNotification(messageTitle: String, messageBody: String) {
        val intent = Intent(this, Productos::class.java) // La actividad que se abrirá al tocar la noti
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        // PendingIntent para versiones nuevas y viejas de Android
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }

        val channelId = "unicafe_novedades_channel" // ID del canal
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Construcción de la notificación
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // ASEGÚRATE DE QUE ESTE ÍCONO EXISTA o usa R.mipmap.ic_launcher
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Desde Android Oreo (API 26) es obligatorio crear un canal de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Novedades Unicafe", // Nombre visible del canal
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Mostrar la notificación (usamos un ID fijo 0 para simplificar)
        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}