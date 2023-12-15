package com.fpoly.smartlunch.ultis

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.fragment.app.Fragment
import com.permissionx.guolindev.PermissionX

fun Activity.startToDetailPermission() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}

fun Fragment.checkPermissionGallery(isAllow: (Boolean) -> Unit) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        PermissionX.init(this)
            .permissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .request { allGranded, _, _ ->
                isAllow(allGranded)
            }
    } else {
        PermissionX.init(this)
            .permissions(
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO
            )
            .request { allGranded, _, _ ->
                isAllow(allGranded)
            }
    }
}

fun Fragment.checkPermisionCallVideo(isAllow: (Boolean) -> Unit) {
    PermissionX.init(this)
        .permissions(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA
        )
        .request { allGranded, _, _ ->
            isAllow(allGranded)
        }
}

fun Fragment.checkPermisionCamera(isAllow: (Boolean) -> Unit) {
    PermissionX.init(this)
        .permissions(
            android.Manifest.permission.CAMERA
        )
        .request { allGranded, _, _ ->
            isAllow(allGranded)
        }
}

fun Fragment.checkRequestPermissions(isAllow: (Boolean) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .request{ allGranted,_,_ ->
                isAllow(allGranted)
            }
    }else{
        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .request{ allGranted,_,_ ->
                isAllow(allGranted)
            }
    }
}

fun Fragment.showPermissionDeniedToast() {
    showSnackbar(
        requireView(),
        "Camera permission denied, please allow permission",
        false,
        ""
    ) {
        activity?.startToDetailPermission()
    }
}