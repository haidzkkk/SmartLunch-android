package com.fpoly.smartlunch.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class ClusterMarker(
    private var position: LatLng,
    private var title: String,
    private var snippet: String,
    var iconPicture: String,
    var user: User
) : ClusterItem {

    fun setPosition(position: LatLng) {
        this.position = position
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun setSnippet(snippet: String) {
        this.snippet = snippet
    }

    override fun getPosition(): LatLng = position

    override fun getTitle(): String = title

    override fun getSnippet(): String = snippet
    override fun getZIndex(): Float = 0f
}
