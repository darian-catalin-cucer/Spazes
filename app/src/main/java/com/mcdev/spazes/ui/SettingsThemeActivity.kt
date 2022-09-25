package com.mcdev.spazes.ui

import android.content.Context
import android.graphics.drawable.DrawableContainer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeActivity
import com.dolatkia.animatedThemeManager.ThemeManager
import com.mcdev.spazes.R
import com.mcdev.spazes.changeStatusBarColor
import com.mcdev.spazes.databinding.ActivitySettingsThemeBinding
import com.mcdev.spazes.makeStatusBarTransparent
import com.mcdev.spazes.theme.BaseTheme
import com.mcdev.spazes.theme.DarkTheme
import com.mcdev.spazes.theme.DefaultTheme
import com.mcdev.spazes.theme.LightTheme
import com.mcdev.spazes.viewmodel.DatastoreViewModel
import com.mcdev.spazes.viewmodel.SpacesViewModel
import com.mcdev.tweeze.util.VerifiedBadge
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.coroutineContext

@AndroidEntryPoint
class SettingsThemeActivity : ThemeActivity() {
    lateinit var binding : ActivitySettingsThemeBinding
    private val dataStoreViewModel: DatastoreViewModel by viewModels()


    override fun getStartTheme(): AppTheme {
        var getTheme : String? = null
        runBlocking {
            getTheme = dataStoreViewModel.readDatastore("themeMode")
        }

        return when (getTheme) {
            "0" -> DefaultTheme()
            "1" -> LightTheme()
            "2" -> DarkTheme()
            else -> {DefaultTheme()}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsThemeBinding.inflate(LayoutInflater.from(this@SettingsThemeActivity))
        setContentView(binding.root)

        makeStatusBarTransparent()

        setupThemeSampleCardView(this, DefaultTheme(), 0)

        binding.changeThemeDefaultBtn.setOnClickListener { ThemeManager.instance.changeTheme(DefaultTheme(), it) }
        binding.changeThemeLightBtn.setOnClickListener { ThemeManager.instance.changeTheme(LightTheme(), it) }
        binding.changeThemeDarkBtn.setOnClickListener { ThemeManager.instance.changeTheme(DarkTheme(), it) }
    }

    private fun setupThemeSampleCardView(context: Context, appTheme: AppTheme, id: Int) {
        val theme = appTheme as BaseTheme
        binding.themeSampleCardView.apply {
//            this.title.text = "Building with Twitter API v2"
//            this.bgSpeaker.setActualImageResource(R.drawable.twitterspacesavipng)
            this.title = "Anime & Chill #GoodVibes #AnimeSpaces"
            this.setSpeakerImageDrawable(R.drawable.spacessampleavi)
            this.isLive = true
            this.participantCount = "6.7K+"
            this.setDisplayName("Spaces", true)
//            this.theme = theme

            //set the colors
//            this.title.setTextColor(theme.textColor(context))
//            this.twitterDisplayNameView.customizeDisplayName.setTextColor(theme.textColor(context))
            //this.titleTextColor = theme.textColor(context)
//            this.setDisplayNameColor(theme.textColor())

//            when (id) {
//                DefaultTheme().id() -> {
////                    this.twitterDisplayNameView.setDisplayName("Spaces", true, VerifiedBadge.WHITE)
////                    this.bgSpeaker.visibility = View.VISIBLE
////                    this.root.setCardBackgroundColor(android.R.color.transparent)
////                    this.speakerAvi.setActualImageResource(R.drawable.twitterspacesavipng)
//                    this.setDisplayName("Spaces", true, VerifiedBadge.WHITE)
//                    this.setCardBgImageDrawable(R.drawable.spacessampleavi)
//                }
//                LightTheme().id() -> {
////                    this.twitterDisplayNameView.setDisplayName("Spaces", true, VerifiedBadge.DEFAULT)
////                    this.bgSpeaker.visibility = View.GONE
////                    this.root.setCardBackgroundColor(theme.cardBg())
//                    this.setDisplayName("Spaces", true, VerifiedBadge.DEFAULT)
//                    this.setCardBgColor(appTheme.cardBg())
//                }
//                DarkTheme().id() -> {
////                    this.twitterDisplayNameView.setDisplayName("Spaces", true, VerifiedBadge.WHITE)
////                    this.bgSpeaker.visibility = View.GONE
////                    this.root.setCardBackgroundColor(theme.cardBg())
//                    this.setDisplayName("Spaces", true, VerifiedBadge.WHITE)
//                    this.setCardBgColor(appTheme.cardBg())
//                }
//
//            }
        }
    }

    override fun syncTheme(appTheme: AppTheme) {
        runBlocking {
            dataStoreViewModel.saveOrUpdateDatastore("themeMode", appTheme.id().toString())
        }
        val theme = appTheme as BaseTheme
        binding.root.setBackgroundColor(appTheme.activityBgColor(this@SettingsThemeActivity))
        setupThemeSampleCardView(this@SettingsThemeActivity, appTheme, appTheme.id())
        binding.themeSampleCardView.theme = theme
    }
}