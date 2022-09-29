package com.mcdev.spazes

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.mcdev.spazes.components.NoSpaceComponent
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

/** Change lottie animation color
 * */
fun LottieAnimationView.changeLayersColor(
    @ColorRes colorRes: Int
) {
    val color = ContextCompat.getColor(context, colorRes)
    val filter = SimpleColorFilter(color)
    val keyPath = KeyPath("**")
    val callback: LottieValueCallback<ColorFilter> = LottieValueCallback(filter)

    addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback)
}

fun String.getOriginalTwitterAvi(): String {
    return this.replace("_normal", "")
}

fun Context.startLoading(swipeRefreshLayout: SwipeRefreshLayout, recyclerMessage: NoSpaceComponent) {
    swipeRefreshLayout.isRefreshing = true
    recyclerMessage.visibility = View.GONE
}

fun Context.stopLoading(swipeRefreshLayout: SwipeRefreshLayout, recyclerMessage: NoSpaceComponent, recyclerView: RecyclerView) {
    swipeRefreshLayout.isRefreshing = false
    recyclerView.visibility = View.VISIBLE
    recyclerMessage.visibility = View.GONE
}

fun Context.showEmpty(swipeRefreshLayout: SwipeRefreshLayout, recyclerMessage: NoSpaceComponent, recyclerView: RecyclerView, message : Int) {
    recyclerMessage.lottieMessage = applicationContext.getString(message)
    swipeRefreshLayout.isRefreshing = false
    recyclerView.visibility = View.GONE
    recyclerMessage.visibility = View.VISIBLE
}