package com.mywings.appschedulling.stats.model

import android.graphics.drawable.Drawable

data class AppMetadata(
    var id: Int = 0,
    var name: String = "",
    var drawable: Drawable? = null,
    var packageName: String = "",
    var localDirectory: String = "",
    var numOfFiles: String = "",
    var size: String = "",
    var show: Boolean = true,
    var synced: Boolean = false,
    var upload: Boolean = false,
    var serverUrl: String = "",
    var imageIcon: String = "",
    var imei: String = ""
)