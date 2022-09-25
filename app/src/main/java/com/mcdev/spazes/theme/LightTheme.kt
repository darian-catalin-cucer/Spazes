package com.mcdev.spazes.theme

import android.content.Context
import androidx.core.content.ContextCompat
import com.mcdev.spazes.R
import com.mcdev.spazes.SpazesThemeMode

class LightTheme : BaseTheme {
    override fun id(): Int = SpazesThemeMode.LIGHT_MODE.value

    override fun textColor(): Int = R.color.light_mode_text_color
//    override fun textColor(context: Context): Int = ContextCompat.getColor(context, R.color.black)

    override fun cardBg(): Int = R.color.light_card_bg
    override fun activityBgColor(context: Context): Int = ContextCompat.getColor(context, R.color.white)
    override fun statusBarColor(): Int = R.color.white

    override fun lottieColor(): Int = R.color.purple_500

    //multi search
    override fun searchTextColor(context: Context): Int = context.resources.getColor(R.color.light_mode_text_color, context.theme)
    override fun searchHintColor(context: Context): Int = context.resources.getColor(R.color.gray_400, context.theme)
    override fun searchClearIconColor(context: Context): Int = context.resources.getColor(R.color.black, context.theme)
    override fun searchIconColor(context: Context): Int = context.resources.getColor(R.color.black, context.theme)
    override fun searchSelectedTabColor(context: Context): Int = context.resources.getColor(R.color.black, context.theme)
}