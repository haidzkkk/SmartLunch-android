package com.fpoly.smartlunch.ultis

object MyConfigNotifi{
    const val CHANNEL_ID = "MY_CHANNEL_ID"
    const val CHANNEL_ID_CHAT = "MY_CHANNEL_ID_CHAT"
    const val CHANNEL_ID_CALL = "MY_CHANNEL_ID_CALL"

    // type để nhận biết loại thông báo muốn giử
    var TYPE_ALL = "TYPE_ALL"
    var TYPE_CHAT = "TYPE_CHAT"
    var TYPE_CALL = "TYPE_CALL"

}

object Status {
    const val ERROR_DIALOG_REQUEST = 9001
    const val PERMISSIONS_REQUEST_ENABLE_GPS =9002
    const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    const val collection_user_locations = "User Locations"
    const val UNCONFIRMED_STATUS: String = "65264bc32d9b3bb388078974"
    const val CONFIRMED_STATUS: String = "65264c102d9b3bb388078976"
    const val DELIVERING_STATUS: String = "65264c672d9b3bb388078978"
    const val SUCCESS_STATUS: String = "6526a6e6adce6a54f6f67d7d"
    const val CANCEL_STATUS: String = "653bc0a72006e5791beab35b"

    const val STATUS_TIEN_MAT = "654892638cd8c0661be05f7c"
    const val STATUS_PAYPAL = "654892778cd8c0661be05f7d"
    const val STATUS_MOMO = "6548928b8cd8c0661be05f7e"
    const val STATUS_VNPAY = "654892938cd8c0661be05f7f"
    const val STATUS_ZALOPAY = "6548929a8cd8c0661be05f80"
}