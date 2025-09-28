package com.esc.begu

import android.Manifest

/**
 * Permisos requeridos para que la aplicación funcione
 */

object Permissions {
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        //Manifest.permission.WRITE_EXTERNAL_STORAGE,
        //Manifest.permission.READ_EXTERNAL_STORAGE
    )
}