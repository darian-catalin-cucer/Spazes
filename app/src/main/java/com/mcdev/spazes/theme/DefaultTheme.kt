package com.mcdev.spazes.theme

import android.content.Context
import androidx.core.content.ContextCompat
import com.mcdev.spazes.R
import com.mcdev.spazes.SpazesThemeMode

class DefaultTheme: BaseTheme {

    override fun id(): Int = SpazesThemeMode.DEFAULT_MODE.value

    override fun textColor(): Int = R.color.white

    override fun cardBg(): Int = android.R.color.transparent
    override fun activityBgColor(context: Context): Int = ContextCompat.getColor(context, R.color.white)
    override fun statusBarColor(): Int = R.color.app_purple

    override fun lottieColor(): Int = R.color.purple_500

}