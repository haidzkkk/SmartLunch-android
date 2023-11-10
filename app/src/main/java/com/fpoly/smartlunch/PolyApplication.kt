package com.fpoly.smartlunch

import android.app.Application
import android.os.Build
import android.widget.Toast
import com.fpoly.smartlunch.di.DaggerPolyComponent
import com.fpoly.smartlunch.di.PolyComponent
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

        setupSdkPaypal()
    }

    private fun setupSdkPaypal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        } else {
            Toast.makeText(this, "Checkout SDK only available for API 23+", Toast.LENGTH_SHORT).show()
        }
    }

}