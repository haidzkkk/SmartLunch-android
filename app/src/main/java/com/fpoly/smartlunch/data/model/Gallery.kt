package com.fpoly.smartlunch.data.model

object GalleryType {
    const val GALLERY_IMAGE = 0
    const val GALLERY_VIDEO = 1
}
interface Gallery{
    val id: Long
    val name: String
    val realPath: String
    val date: Long
    val type: Int
}

public data class GalleyImage(
    override val id: Long,
    override val name: String,
    override val realPath: String,
    override val date: Long,
    override val type: Int
): Gallery

public data class GalleryVideo(
    override val id: Long,
    override val name: String,
    override val realPath: String,
    val thumb: String,
    override val date: Long,
    override val type: Int
): Gallery