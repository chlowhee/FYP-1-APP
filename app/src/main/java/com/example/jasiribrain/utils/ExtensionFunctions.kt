package com.example.jasiribrain.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.getMissingPermissions(vararg permissions: String): Array<String>{
    val missingPermission = mutableListOf<String>()
    permissions.forEach { p ->
        if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
            missingPermission.add(p)
        }
    }
    return missingPermission.toTypedArray()
}