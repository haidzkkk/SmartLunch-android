package com.fpoly.smartlunch.ui.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FcmService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        var notifi: RemoteMessage.Notification? = message.notification

        Log.e("FCMService", "onMessageReceived: " + notifi?.title + notifi?.body);
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("FCMService token", "onMessageReceived: $token");
    }
}