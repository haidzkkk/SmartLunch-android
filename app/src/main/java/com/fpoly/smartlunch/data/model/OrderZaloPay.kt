package com.fpoly.smartlunch.data.model

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.fpoly.smartlunch.data.model.Helpers.getMac
import com.fpoly.smartlunch.ultis.StringUltis
import com.fpoly.smartlunch.ultis.convertDateToStringFormat
import okhttp3.FormBody
import okhttp3.RequestBody
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.LinkedList
import java.util.Objects
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

data class OrderZaloPayReponse(
    val return_code: String,
    val zp_trans_token: String,
)

data class StatusOrderZaloPayReponse(
    val amount: Double?,
    val zp_trans_id: String?,
)

data class RefundOrderZaloPayReponse(
    val return_code: Int,
    val return_message: String,
    val sub_return_code: String,
    val sub_return_message: String,
    val refund_id: String,
)

data class OrderZaloPayRequest(
    val app_id: String,
    val app_user: String,
    val app_time: String,
    val amount: String,
    val app_trans_id: String,
    val embed_data: String,
    val item: String,
    val bank_code: String,
    val description: String,
    val mac: String,
) {
    companion object {
        fun createOrder(amount: String, idOrder: (id: String) -> Unit): RequestBody {
            val app_id = ZaloPayInfo.APP_ID.toString()
            val app_user = "Android_Demo";
            val app_time = Date().time.toString()
            val app_trans_id = Helpers.appTransId
            val embed_data = "{}";
            val item = "[]";
            val bank_code = "zalopayapp";
            val description = "Merchant pay for order #" + Helpers.appTransId
            val inputHMac = String.format(
                "%s|%s|%s|%s|%s|%s|%s",
                app_id,
                app_trans_id,
                app_user,
                amount,
                app_time,
                embed_data,
                item
            )
            val mac = getMac(ZaloPayInfo.MAC_KEY, inputHMac)!!

            idOrder(app_trans_id)

            return FormBody.Builder()
                .add("app_id", app_id)
                .add("app_user", app_user)
                .add("app_time", app_time)
                .add("amount", amount)
                .add("app_trans_id", app_trans_id)
                .add("embed_data", embed_data)
                .add("item", item)
                .add("bank_code", bank_code)
                .add("description", description)
                .add("mac", mac)
                .build()
        }

        fun queryStatusOrderZalo(app_trans_id: String): RequestBody {
            val app_id = ZaloPayInfo.APP_ID.toString()
            val inputHMac = String.format("%s|%s|%s", app_id, app_trans_id, ZaloPayInfo.MAC_KEY)
            val mac = getMac(ZaloPayInfo.MAC_KEY, inputHMac)!!

            return FormBody.Builder()
                .add("app_id", app_id)
                .add("app_trans_id", app_trans_id)
                .add("mac", mac)
                .build()
        }

        fun refundOrderZalo(zp_trans_id: String, amount: Double): RequestBody {
            val app_id = ZaloPayInfo.APP_ID.toString()
            val timestamp = System.currentTimeMillis()
            val m_refund_id = String.format("%s_%s_%s",
                Date().convertDateToStringFormat(StringUltis.dateZaloTimeFormat),
                app_id,
                "$timestamp${Random.Default.nextInt(111,999)}")
            val description = "canncel"
            val inputHMac = String.format("%s|%s|%s|%s|%s", app_id, zp_trans_id, amount.toInt().toString(), description, timestamp )
            Log.e(":", "inputHMac: $inputHMac", )
            val mac = getMac(ZaloPayInfo.MAC_KEY, inputHMac)!!

            return FormBody.Builder()
                .add("m_refund_id", m_refund_id)
                .add("app_id", app_id)
                .add("zp_trans_id", zp_trans_id)
                .add("amount", amount.toInt().toString())
                .add("timestamp", timestamp.toString())
                .add("description", description)
                .add("mac", mac)
                .build()
        }
    }
}

object ZaloPayInfo{
    const val APP_ID = 2554
    const val MAC_KEY = "sdngKKJmqEMzvh5QQcdD2A9XBSKUNaYn"
    const val URL_CREATE_ORDER = "https://sb-openapi.zalopay.vn"
}
object Helpers {
    private var transIdDefault = 1

    val appTransId: String
        get() {
            if (transIdDefault >= 100000) {
                transIdDefault = 1
            }
            transIdDefault += 1
            @SuppressLint("SimpleDateFormat") val formatDateTime = SimpleDateFormat("yyMMdd_hhmmss")
            val timeString: String = formatDateTime.format(Date())
            return String.format("%s%06d", timeString, transIdDefault)
        }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun getMac(key: String, data: String): String? {
        return Objects.requireNonNull(HMacUtil.HMacHexStringEncode(HMacUtil.HMACSHA256, key, data))
    }
}


object HMacUtil {
    const val HMACMD5 = "HmacMD5"
    const val HMACSHA1 = "HmacSHA1"
    const val HMACSHA256 = "HmacSHA256"
    const val HMACSHA512 = "HmacSHA512"
    val UTF8CHARSET = StandardCharsets.UTF_8
    val HMACS = LinkedList(
        mutableListOf(
            "UnSupport",
            "HmacSHA256",
            "HmacMD5",
            "HmacSHA384",
            "HMacSHA1",
            "HmacSHA512"
        )
    )

    // @formatter:on
    private fun HMacEncode(algorithm: String, key: String, data: String): ByteArray? {
        var macGenerator: Mac? = null
        try {
            macGenerator = Mac.getInstance(algorithm)
            val signingKey = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), algorithm)
            macGenerator.init(signingKey)
        } catch (ex: Exception) {
        }
        if (macGenerator == null) {
            return null
        }
        var dataByte: ByteArray? = null
        try {
            dataByte = data.toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
        }
        return macGenerator.doFinal(dataByte)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun HMacBase64Encode(algorithm: String, key: String, data: String): String? {
        val hmacEncodeBytes = HMacEncode(algorithm, key, data) ?: return null
        return Base64.getEncoder().encodeToString(hmacEncodeBytes)
    }

    fun HMacHexStringEncode(algorithm: String, key: String, data: String): String? {
        val hmacEncodeBytes = HMacEncode(algorithm, key, data) ?: return null
        return HexStringUtil.byteArrayToHexString(hmacEncodeBytes)
    }
}


object HexStringUtil {
    private val HEX_CHAR_TABLE = byteArrayOf(
        '0'.code.toByte(),
        '1'.code.toByte(),
        '2'.code.toByte(),
        '3'.code.toByte(),
        '4'.code.toByte(),
        '5'.code.toByte(),
        '6'.code.toByte(),
        '7'.code.toByte(),
        '8'.code.toByte(),
        '9'.code.toByte(),
        'a'.code.toByte(),
        'b'.code.toByte(),
        'c'.code.toByte(),
        'd'.code.toByte(),
        'e'.code.toByte(),
        'f'.code.toByte()
    )

    fun byteArrayToHexString(raw: ByteArray): String {
        val hex = ByteArray(2 * raw.size)
        var index = 0
        for (b in raw) {
            val v = b.toInt() and 0xFF
            hex[index++] = HEX_CHAR_TABLE[v ushr 4]
            hex[index++] = HEX_CHAR_TABLE[v and 0xF]
        }
        return hex.toString(Charsets.UTF_8)
    }

//    fun hexStringToByteArray(hex: String): ByteArray {
//        val hexstandard = hex.lowercase(Locale.ENGLISH)
//        val sz = hexstandard.length / 2
//        val bytesResult = ByteArray(sz)
//        var idx = 0
//        for (i in 0 until sz) {
//            bytesResult[i] = hexstandard[idx].code.toByte()
//            ++idx
//            var tmp = hexstandard[idx].code.toByte()
//            ++idx
//            if (bytesResult[i] > HEX_CHAR_TABLE[9]) {
//                (bytesResult[i] -= ('a'.code.toByte() - 10).toByte()).toByte()
//            } else {
//                (bytesResult[i] -= '0'.code.toByte()).toByte()
//            }
//            if (tmp > HEX_CHAR_TABLE[9]) {
//                (tmp -= ('a'.code.toByte() - 10).toByte()).toByte()
//            } else {
//                (tmp -= '0'.code.toByte()).toByte()
//            }
//            bytesResult[i] = (bytesResult[i] * 16 + tmp).toByte()
//        }
//        return bytesResult
//    }
}

