package com.avallie.helpers


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.avallie.model.BudgetNotificationData
import com.avallie.view.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseInstanceIDService : FirebaseMessagingService() {

    enum class NotificationType {
        BUDGET_RECEIVED,
        DEFAULT
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        sendMyNotification(message)
    }

    private fun sendMyNotification(message: RemoteMessage) {
        createNotificationChannel()

        val intent = Intent(this, MainActivity::class.java)

        when (getNotificationType(message)) {
            NotificationType.BUDGET_RECEIVED -> {
                intent.putExtra(
                    "budget_notification_data",
                    BudgetNotificationData(
                        message.data["budgetId"]!!.toLong(),
                        message.data["selectedProductId"]!!.toLong()
                    )
                )
            }
            else -> {

            }
        }

        val pendingIntent =
            PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, "Avallie Channel Id")
            .setSmallIcon(com.avallie.R.drawable.logo_avallie)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun getNotificationType(message: RemoteMessage): NotificationType {
        return if (message.data.containsKey("budgetId")) {
            NotificationType.BUDGET_RECEIVED
        } else {
            NotificationType.DEFAULT
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(com.avallie.R.string.app_name)
            val description = getString(com.avallie.R.string.notification_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Avallie Channel Id", name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

}