package com.mateomalaj.appkapkextractor

import android.content.pm.ApplicationInfo

data class ApkModel(
    val appinfo: ApplicationInfo,
    val appname: String,
    val packagename: String? = "",
    val version: String? = "",
    val installdate: Long? = null,
    val lastupdated: Long? = null
)
