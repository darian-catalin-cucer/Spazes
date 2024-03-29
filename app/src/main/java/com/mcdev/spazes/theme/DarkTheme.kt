package com.mcdev.spazes.theme

import android.content.Context
import androidx.core.content.ContextCompat
import com.mcdev.spazes.R
import com.mcdev.spazes.SpazesThemeMode

class DarkTheme: BaseTheme {

    override fun id(): Int = SpazesThemeMode.DARK_MODE.value

    //card
    override fun cardTextColor(): Int = R.color.dark_mode_text_color
    override fun cardBg(): Int = R.color.light_dark

    //activity
    override fun activityBgColor(context: Context): Int = ContextCompat.getColor(context, R.color.black)
    override fun statusBarColor(): Int = R.color.black
    override fun textColor(): Int = R.color.dark_mode_text_color

    override fun lottieColor(): Int = R.color.gray_400

    //multi search
    override fun searchTextColor(context: Context): Int = context.resources.getColor(R.color.white, context.theme)
    override fun searchHintColor(context: Context): Int = context.resources.getColor(R.color.gray_400, context.theme)
    override fun searchClearIconColor(context: Context): Int = context.resources.getColor(R.color.white, context.theme)
    override fun searchIconColor(context: Context): Int = context.resources.getColor(R.color.white, context.theme)
    override fun searchSelectedTabColor(context: Context): Int = context.resources.getColor(R.color.gray_600, context.theme)
}