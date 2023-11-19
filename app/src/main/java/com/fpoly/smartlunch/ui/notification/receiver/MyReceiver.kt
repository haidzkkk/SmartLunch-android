package com.fpoly.smartlunch.ui.notification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fpoly.smartlunch.ui.main.MainActivity

class MyReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action: Int? = intent?.getIntExtra("notification_action_broadcast", 0)

        if (action != null) {
            val broadcastIntent = Intent("com.fpoly.smartlunch.NEW_DATA_AVAILABLE")
            context?.sendBroadcast(broadcastIntent)
        }
    }
}

