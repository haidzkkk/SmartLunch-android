package com.fpoly.smartlunch.ui.notification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.fpoly.smartlunch.ui.main.MainActivity

class MyReceiver: BroadcastReceiver() {
    companion object{
        var actionNotify = "com.fpoly.smartlunch.NEW_DATA_AVAILABLE"
        var actionCall = "com.fpoly.smartlunch.CALL_VIDEO"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action: Int? = intent?.getIntExtra("notification_action_broadcast", 0)

        when(action){
            1 ->{
                val broadcastIntent = Intent(actionNotify)
                context?.sendBroadcast(broadcastIntent)
            }
            2 ->{
                val broadcastIntent = Intent(actionCall)

                var type = intent.getStringExtra("type")
                var idUrl = intent.getStringExtra("idUrl")
                broadcastIntent.apply {
                    putExtras(Bundle().apply {
                        putString("type", type)
                        putString("idUrl", idUrl)
                    })
                }
                context?.sendBroadcast(broadcastIntent)
            }
        }
    }
}

