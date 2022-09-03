package com.mcdev.spazes.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType
import com.mcdev.spazes.R
import com.mcdev.spazes.viewmodel.SpacesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class AppIntro: AppIntro2() {
    private val viewModel: SpacesViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // do not call setContentView

        setImmersiveMode() // immersive mode to make wizard fullscreen
        isColorTransitionsEnabled = true // enable color transition
        setProgressIndicator() // enable progress indicator instead of dot indicator
        isSystemBackButtonLocked = true // lock back button
        isSkipButtonEnabled = false // disable skip button
        setNextArrowColor(getColor(R.color.purple_200)) // set color for the next arrow button
        //setDoneText(getString(R.string.get_started))
        setStatusBarColorRes(android.R.color.transparent)

        // Change Indicator Color
        setIndicatorColor(
            selectedIndicatorColor = getColor(R.color.purple_200),
            unselectedIndicatorColor = getColor(R.color.purple_200)
        )

        setTransformer(AppIntroPageTransformerType.Parallax(// set parallax effect
            titleParallaxFactor = 1.0,
            imageParallaxFactor = -1.0,
            descriptionParallaxFactor = 2.0
        ))

        addSlide(AppIntroFragment.createInstance(
            title = getString(R.string.search_and_find),
            titleColorRes = R.color.app_purple,
            description = getString(R.string.search_and_find_details),
            descriptionColorRes = R.color.black,
            imageDrawable = R.drawable.inky_search,
            backgroundColorRes = R.color.white
        ))

        addSlide(AppIntroFragment.createInstance(
            title = getString(R.string.live_spaces_title),
            titleColorRes = R.color.white,
            description = getString(R.string.live_spaces_details),
            descriptionColorRes = R.color.white,
            imageDrawable = R.drawable.inky_podcast,
            backgroundColorRes = android.R.color.holo_purple
        ))

        addSlide(AppIntroFragment.createInstance(
            title = getString(R.string.follow_hosts_title),
            titleColorRes = R.color.app_purple,
            description = getString(R.string.follow_hosts_details),
            descriptionColorRes = R.color.black,
            imageDrawable = R.drawable.inky_like,
            backgroundColorRes = R.color.white
        ))

        addSlide(AppIntroFragment.createInstance(
            title = getString(R.string.all_in_one_placea_title),
            titleColorRes = R.color.white,
            description = getString(R.string.all_in_one_place_details),
            descriptionColorRes = R.color.white,
            imageDrawable = R.drawable.inky_tech_support,
            backgroundColorRes = android.R.color.holo_purple
        ))
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Decide what to do when the user clicks on "Skip"
        //finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Decide what to do when the user clicks on "Done"

        runBlocking {
            viewModel.updateAppIntroDatastore("show_app_intro", false)
        }

        finish()
    }
}