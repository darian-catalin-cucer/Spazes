package com.mcdev.spazes.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieDrawable
import com.dolatkia.animatedThemeManager.AppTheme
import com.mcdev.spazes.R
import com.mcdev.spazes.SpazesThemeMode
import com.mcdev.spazes.databinding.SpaceItemV2Binding
import com.mcdev.spazes.theme.DarkTheme
import com.mcdev.spazes.theme.DefaultTheme
import com.mcdev.spazes.theme.LightTheme
import com.mcdev.tweeze.util.VerifiedBadge

class SpaceCardComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int = 0
) : CardView(context, attrs, defStyle) {

    private val binding: SpaceItemV2Binding = SpaceItemV2Binding.inflate(
        LayoutInflater.from(context),
        this, true
    )

    var theme : AppTheme = DefaultTheme()
        set(value) {
            when (value) {
                is DefaultTheme -> {
                    binding.liveViews.background = ContextCompat.getDrawable(context, R.drawable.rounded_bg)
                    binding.stateLiveView.apply {
                        repeatCount = LottieDrawable.INFINITE
                        setAnimation(R.raw.bluewaveform)
                        playAnimation()
                    }
                }

                is LightTheme -> {
                    binding.liveViews.background = ContextCompat.getDrawable(context, R.drawable.rounded_bg)
                    binding.stateLiveView.apply {
                        repeatCount = LottieDrawable.INFINITE
                        setAnimation(R.raw.bluewaveform)
                        playAnimation()
                    }
                }

                is DarkTheme -> {
                    binding.liveViews.background = ContextCompat.getDrawable(context, R.drawable.live_view_bg_dark)
                    binding.participantCount.setTextColor(context.resources.getColor(R.color.gray_600, context.theme))
                    binding.stateLiveView.apply {
                        repeatCount = LottieDrawable.INFINITE
                        setAnimation(R.raw.waveform_white)
                        playAnimation()
                    }
                }
            }
            field = value
        }

    var title : String? = null
        set(value) {
            binding.title.text = value
            field = value
        }

    var titleTextColor : Int = R.color.white
        set(value) {
            binding.title.setTextColor(value)
        }

    var isLive : Boolean = false
        set(value) {
            if (value) {
                binding.liveViews.visibility = View.VISIBLE
                binding.stateScheduledView.visibility = View.GONE
            } else {
                binding.liveViews.visibility = View.GONE
                binding.stateScheduledView.visibility = View.VISIBLE
            }
            field = value
        }

    var participantCount : CharSequence = ""
        set(value)  {
            binding.participantCount.text = value
            field = value
        }

    var scheduledDate : String? = null
        set(value) {
            binding.stateScheduledView.text = value
            field = value
        }
//    private fun init(attrs: AttributeSet?, defStyle: Int) {
//        // Load attributes
//        val a = context.obtainStyledAttributes(
//            attrs, R.styleable.NoSpaceComponent, defStyle, 0
//        )
//
//        a.recycle()
//    }

    fun setDisplayName(displayName: String, verified: Boolean, verifiedBadge: VerifiedBadge = VerifiedBadge.DEFAULT) {
        binding.twitterDisplayNameView.setDisplayName(displayName, verified, verifiedBadge)
    }

    fun setDisplayNameColor(color: Int) {
        binding.twitterDisplayNameView.customizeDisplayName.setTextColor(color)
    }

    fun setSpeakerImageUri(uriString:  MutableList<String>) {
        binding.speakerAvi.setImageURI(uriString[0])
        when {
            uriString.size == 2 -> {
                binding.speakerAvi.setImageURI(uriString[1])
            }
            uriString.size >= 2 -> {
                    binding.speakerOneAvi.setImageURI(uriString[1])
                    binding.speakerTwoAvi.setImageURI(uriString[2])
            }
            else -> {
//                set these null values to prevent duplicates
                    binding.speakerOneAvi.setImageURI("null")
                    binding.speakerTwoAvi.setImageURI("null")
            }
        }
    }

    fun setSpeakerImageDrawable(resourceDrawable: Int) {
        binding.speakerAvi.setActualImageResource(resourceDrawable)
    }

    fun setCardBgImageUri(uriString: String?) {
        binding.bgSpeaker.setImageURI(uriString)
        binding.bgSpeaker.visibility = View.VISIBLE
    }

    fun setCardBgImageDrawable(resourceDrawable: Int) {
        binding.bgSpeaker.setActualImageResource(resourceDrawable)
        binding.bgSpeaker.visibility = View.VISIBLE
    }

    fun setCardBgColor(int: Int) {
        binding.bgSpeaker.visibility = View.GONE
        binding.root.setCardBackgroundColor(context.resources.getColor(int, context.theme))
    }
}