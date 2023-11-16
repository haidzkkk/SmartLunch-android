package com.fpoly.smartlunch.ultis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.model.ClusterMarker
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import de.hdodenhof.circleimageview.CircleImageView

class MyClusterManagerRenderer(
    context: Context,
    googleMap: GoogleMap,
    clusterManager: ClusterManager<ClusterMarker>
) : DefaultClusterRenderer<ClusterMarker>(context, googleMap, clusterManager) {

    private val iconGenerator: IconGenerator
    private val imageView: CircleImageView
    private val markerCacheManager = MarkerCacheManager()

    init {
        imageView = CircleImageView(context.applicationContext)
        val markerWidth = context.resources.getDimension(R.dimen.custom_marker_image).toInt()
        val markerHeight = context.resources.getDimension(R.dimen.custom_marker_image).toInt()
        imageView.layoutParams = ViewGroup.LayoutParams(markerWidth, markerHeight)
        val padding = context.resources.getDimension(R.dimen.custom_marker_padding).toInt()
        imageView.setPadding(padding, padding, padding, padding)
        imageView.circleBackgroundColor = Color.RED

        iconGenerator = IconGenerator(context.applicationContext)
        iconGenerator.setStyle(IconGenerator.STYLE_ORANGE)
        iconGenerator.setBackground(null)
        iconGenerator.setContentView(imageView)
    }

    override fun onBeforeClusterItemRendered(item: ClusterMarker, markerOptions: MarkerOptions) {
        val cachedBitmap = markerCacheManager.getMarkerFromCache(item.title)

        if (cachedBitmap != null) {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(cachedBitmap)).title(item.title)
        } else {
            Glide.with(imageView.context)
                .asBitmap()
                .load(item.iconPicture)
                .placeholder(R.drawable.ellipse)
                .error(R.drawable.ellipse)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        imageView.setImageBitmap(resource)
                        val icon: Bitmap = iconGenerator.makeIcon()
                        markerCacheManager.addMarkerToCache(item.title, icon)
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.title)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }

    override fun shouldRenderAsCluster(cluster: Cluster<ClusterMarker>): Boolean {
        return false
    }

    inner class MarkerCacheManager {
        private val markerCache: MutableMap<String, Bitmap> = mutableMapOf()

        fun getMarkerFromCache(key: String): Bitmap? {
            return markerCache[key]
        }

        fun addMarkerToCache(key: String, bitmap: Bitmap) {
            markerCache[key] = bitmap
        }

        fun clearCache() {
            markerCache.clear()
        }
    }
}
