package com.mcdev.spazes.theme

import android.content.Context
import androidx.core.content.ContextCompat
import com.mcdev.spazes.R
import com.mcdev.spazes.SpazesThemeMode

class DefaultTheme: BaseTheme {

    override fun id(): Int = SpazesThemeMode.DEFAULT_MODE.value

    override fun textColor(): Int = R.color.white

    override fun cardBg(): Int = android.R.color.transparent
    override fun activityBgColor(context: Context): Int = ContextCompat.getColor(context, R.color.light_activity_bg_color)
    override fun statusBarColor(): Int = R.color.light_activity_bg_color

    override fun lottieColor(): Int = R.color.purple_500

    //multi search
    override fun searchTextColor(context: Context): Int = context.resources.getColor(R.color.black, context.theme)
    override fun searchHintColor(context: Context): Int = context.resources.getColor(R.color.search_hint_text_color, context.theme)
    override fun searchClearIconColor(context: Context): Int = context.resources.getColor(R.color.black, context.theme)
    override fun searchIconColor(context: Context): Int = context.resources.getColor(R.color.black, context.theme)
    override fun searchSelectedTabColor(context: Context): Int = context.resources.getColor(R.color.custom_purple, context.theme)
}