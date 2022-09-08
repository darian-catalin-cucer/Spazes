package com.mcdev.spazes.theme

import android.content.Context
import com.dolatkia.animatedThemeManager.AppTheme

interface BaseTheme : AppTheme {
    fun textColor(context: Context): Int

    //cards
    fun cardBg(): Int

    fun activityBgColor(context: Context): Int

    fun statusBarColor(): Int
}