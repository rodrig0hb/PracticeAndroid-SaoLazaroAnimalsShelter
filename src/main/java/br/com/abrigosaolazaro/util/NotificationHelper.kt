package br.com.abrigosaolazaro.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import br.com.abrigosaolazaro.AbrigoApplication
import br.com.abrigosaolazaro.MainActivity

object NotificationHelper {

    private const val ROUTE_NOTIFICATION_ID = 1001

    /** Dispara uma notificação persistente de acompanhamento de rota. */
    fun showRouteNotification(
        context : Context,
        distance: String,
        duration: String
    ) {
        val tapIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, AbrigoApplication.ROUTE_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("🐾 Rota ao Abrigo São Lázaro")
            .setContentText("$distance • ~$duration")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Você está a $distance do Abrigo São Lázaro.\nTempo estimado: $duration.\nAv. Eng. Luiz Montenegro, 430 – Siqueira, Fortaleza-CE")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(tapIntent)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(ROUTE_NOTIFICATION_ID, notification)
    }

    /** Cancela a notificação de rota. */
    fun cancelRouteNotification(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(ROUTE_NOTIFICATION_ID)
    }
}
