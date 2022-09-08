package com.mcdev.spazes.theme

import android.content.Context
import androidx.core.content.ContextCompat
import com.mcdev.spazes.R
import com.mcdev.spazes.SpazesThemeMode

class LightTheme : BaseTheme {
    override fun id(): Int = SpazesThemeMode.LIGHT_MODE.value

    override fun textColor(context: Context): Int = ContextCompat.getColor(context, android.R.color.black)

    override fun cardBg(): Int = R.color.white
    override fun activityBgColor(context: Context): Int = ContextCompat.getColor(context, R.color.light_activity_bg_color)
    override fun statusBarColor(): Int = R.color.white


}