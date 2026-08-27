package br.com.abrigosaolazaro

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import br.com.abrigosaolazaro.data.db.AppDatabase

class AbrigoApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ROUTE_CHANNEL_ID,
                "Rastreamento de Rota",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificações de acompanhamento de rota ao Abrigo São Lázaro"
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        const val ROUTE_CHANNEL_ID = "route_tracking"
    }
}
