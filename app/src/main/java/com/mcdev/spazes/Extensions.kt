package com.mcdev.spazes

import android.app.Activity
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.net.toUri
import java.text.SimpleDateFormat

fun String.formatDateAndTime(): String {
    val INCOMING_DT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"
    val DISPLAY_DT_PATTERN = "dd MMM yy \nhh:mm a"
    val incomingFormat = SimpleDateFormat(INCOMING_DT_PATTERN)
    val incDate = incomingFormat.parse(this)
    val displayFormat: SimpleDateFormat = SimpleDateFormat(DISPLAY_DT_PATTERN)

    val date = displayFormat.format(incDate!!)
    return date
}

fun Activity.makeStatusBarTransparent() {
    window.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        statusBarColor = Color.TRANSPARENT
    }
}

fun Activity.changeStatusBarColor(color: Int) {
    val window: Window = this.window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.statusBarColor = resources.getColor(color, this.theme)
    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

}

fun String.getOriginalTwitterAvi(): String {
    return this.replace("_normal", "")
}