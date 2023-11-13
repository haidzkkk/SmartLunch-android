package com.fpoly.smartlunch.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class ClusterMarker(
    private var position: LatLng,
    private var title: String, snippet: String, iconPicture: String, user: User
) :
    ClusterItem {
    private var snippet
            : String
    var iconPicture: String
    var user: User

    init {
        title = title
        this.snippet = snippet
        this.iconPicture = iconPicture
        this.user = user
    }

    fun setPosition(position: LatLng) {
        this.position = position
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun setSnippet(snippet: String) {
        this.snippet = snippet
    }

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        return snippet
    }

    override fun getZIndex(): Float {
        return 0f
    }
}