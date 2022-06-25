package com.mcdev.spazes

import android.app.Application
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.mcdev.spazes.ui.HomeActivity
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
            .enabled(true) //default: true
            .showErrorDetails(false) //default: true
            .showRestartButton(true) //default: true
            .logErrorOnRestart(false) //default: true
            .trackActivities(false) //default: false
            .minTimeBetweenCrashesMs(2000) //default: 3000
//            .errorDrawable(R.drawable.ic_custom_drawable) //default: bug image
            .restartActivity(HomeActivity::class.java) //default: null (your app's launch activity)
//                .errorActivity(YourCustomErrorActivity.class) //default: null (default error activity)
//                    .eventListener(new YourCustomEventListener()) //default: null
//                    .customCrashDataCollector(new YourCustomCrashDataCollector()) //default: null
            .apply()
    }
}