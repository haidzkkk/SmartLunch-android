package com.fpoly.smartlunch

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.fpoly.smartlunch.di.DaggerPolyComponent
import com.fpoly.smartlunch.di.PolyComponent
import com.fpoly.smartlunch.ultis.MyConfigNotifi
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.config.SettingsConfig
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.UserAction

class PolyApplication : Application() {

    val polyComponent: PolyComponent by lazy {
        DaggerPolyComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        polyComponent.inject(this)
        setupChannelNotifi()
        setupSdkPaypal()
        setupSdkFb()
    }

    private fun setupSdkFb() {
        FacebookSdk.sdkInitialize(applicationContext);
        AppEventsLogger.activateApp(this);
    }

    private fun setupChannelNotifi() {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            val channelAll = NotificationChannel(MyConfigNotifi.CHANNEL_ID, "Thông báo tổng", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channelAll)

            val channelChat = NotificationChannel(MyConfigNotifi.CHANNEL_ID_CHAT, "Đoạn chat", NotificationManager.IMPORTANCE_HIGH)
            val uriSoundChat = Uri.parse("android.resource://" + packageName + "/" + R.raw.sound_messager)
            val audioAttr = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
            channelChat.setSound(uriSoundChat, audioAttr)
            notificationManager.createNotificationChannel(channelChat)

            val channelCall = NotificationChannel(MyConfigNotifi.CHANNEL_ID_CALL, "Cuộc gọi", NotificationManager.IMPORTANCE_HIGH)
            channelChat.setSound(
                Uri.parse("android.resource://" + packageName + "/" + R.raw.sound_call),
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
            )
            notificationManager.createNotificationChannel(channelCall)
        }
    }

    private fun setupSdkPaypal() {
        var client_id = "ARJpWyaKGrxD1lZZA8mdFkGgwmTGSOp1K_-yJ6JcyQg-GeqMbYQ9pyATDFSgXLMvC3RWmehKuiuzlfTe"
        var returnUrl = "com.fpoly.smartlunch://paypalpay"
        PayPalCheckout.setConfig(
            CheckoutConfig(
                application = this,
                clientId = client_id,
                environment = Environment.SANDBOX,
                returnUrl = returnUrl,
                currencyCode = CurrencyCode.USD,
                userAction = UserAction.PAY_NOW,
                settingsConfig = SettingsConfig(
                    loggingEnabled = true
                )
            )
        )
    }

}