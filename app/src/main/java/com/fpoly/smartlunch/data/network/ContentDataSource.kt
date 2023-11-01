package com.fpoly.smartlunch.data.network

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.fpoly.smartlunch.data.model.Gallery
import com.fpoly.smartlunch.data.model.GalleryType
import com.fpoly.smartlunch.data.model.GalleryVideo
import com.fpoly.smartlunch.data.model.GalleyImage
import javax.inject.Inject


class ContentDataSource @Inject constructor(
    private val contentResolver: ContentResolver
) {

    fun getImageAndVideo():  ArrayList<Gallery> {
        val list: ArrayList<Gallery> = arrayListOf()
        list.addAll(getImage())
        list.addAll(getVideo())
        return list;
    }

    @SuppressLint("Range")
    public fun getImage(): ArrayList<Gallery> {
        var listImage = arrayListOf<Gallery>()

        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        var projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.Media.DATE_ADDED
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        var cursor = contentResolver.query(uri, projection, null, null, sortOrder)
        while (cursor != null && cursor.moveToNext()) {
            var id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
            var name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
            var realPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
            val date = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))
            listImage.add(GalleyImage(id, name, realPath, date, GalleryType.GALLERY_IMAGE))
        }
        cursor?.close()
        return listImage
    }


    @SuppressLint("Range")
    public fun getVideo(): ArrayList<Gallery> {
        var listVideo = arrayListOf<Gallery>()

        val uri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        var projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DATA,
            MediaStore.Video.Thumbnails.DATA,
            MediaStore.Video.Media.DATE_ADDED
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        var cursor = contentResolver.query(uri, projection, null, null, sortOrder)
        while (cursor != null && cursor.moveToNext()) {
            var id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID))
            var name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
            var realPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
            var thumb = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA))
            val date = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))

            listVideo.add(GalleryVideo(id, name, realPath, thumb, date, GalleryType.GALLERY_VIDEO))
        }
        cursor?.close()
        return listVideo
    }
}
