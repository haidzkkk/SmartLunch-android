package com.fpoly.smartlunch.data.model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.Date

data class UserLocation(
    var user: User? = null,
    var geoPoint: GeoPoint? = null,
    @ServerTimestamp
    var timestamp: Date? = null
) : Serializable
